package vn.edu.clevai.bplog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.service.BpUsiUserItemService;
import vn.edu.clevai.bplog.service.Cep100UserService;
import vn.edu.clevai.bplog.service.PodService;
import vn.edu.clevai.bplog.service.TeacherPodMigrationService;
import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.proxy.bplog.constant.USTEnum;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpPODResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpUsiUserItemResponse;
import vn.edu.clevai.common.proxy.user.payload.response.UserAccountResponse;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class TeacherPodMigrationServiceImpl implements TeacherPodMigrationService {
	@Autowired
	private Cep100UserService cep100UserService;

	@Autowired
	private BpUsiUserItemService bpUsiUserItemService;

	@Autowired
	private PodService podService;

	@Override
	public List<BpPODResponse> migrateSingleTeacher(String username, String ust) {
		UserAccountResponse xteacher = cep100UserService.findTeacherFromX(username);

		log.info("Started migrating data for xteacher = " + xteacher.getUsername());

		BpUsiUserItemResponse response = bpUsiUserItemService.createOrUpdateUsi(
				xteacher.getUsername(),
				xteacher.getLastName(),
				xteacher.getFirstName(),
				ust, /* HiepNV's CR. */
				xteacher.getUsername(),
				xteacher.getFullName(),
				xteacher.getPhone(),
				xteacher.getEmail()
		);

		String myprd = "AT";
		Date fromDate = new Date(xteacher.getCreatedAt().getTime());
		Date toDate = new Date(200, 11, 31); // 2100-12-31
		String pattern = "yyyyMMdd";
		DateFormat df = new SimpleDateFormat(pattern);

		List<BpPODResponse> output = new ArrayList<>();

		for (String mypt : Arrays.asList("BC", "PM", "MD", "GF", "TP10", "TPU", "PV", "OM")) {
			String code = String.join("-", mypt, myprd, response.getCode(), df.format(fromDate));

			output.add(
					podService.setPOD(
							code,
							mypt,
							response.getCode(),
							myprd,
							fromDate,
							toDate,
							null
					)
			);
		}

		log.info("Finished migrating data for xteacher = " + xteacher.getUsername());

		return output;
	}

	@Override
	public void migrateTeacherPods(List<UserAccountResponse> teachers) {
		for (UserAccountResponse teacher : teachers) {
			try {
				migrateSingleTeacher(teacher.getUsername(), USTEnum.TE.getName());
			} catch (Exception e) {
				log.error("Failed to migrate xteacher = " + teacher.getUsername(), e);
			}
		}
	}

	@Override
	public void migrateTeacherPods(
			Integer page,
			Integer size
	) {
		GeneralPageResponse<UserAccountResponse> teachers = cep100UserService.findTeachers(
				null,
				page,
				size
		);

		migrateTeacherPods(teachers.getContent());
	}

	@Override
	public void migrateAllTeacherPods() {
		int page = 0;
		int size = 100;

		while (true) {
			GeneralPageResponse<UserAccountResponse> teachers = cep100UserService.findTeachers(
					null,
					page,
					size
			);

			log.info("MigratePODTE page {} / {}", page + 1, teachers.getTotalPages());

			migrateTeacherPods(teachers.getContent());

			log.info("Finished migrating PODTE page {} / {}", page + 1, teachers.getTotalPages());

			page++;

			if (teachers.isLast()) {
				break;
			}
		}
	}
}
