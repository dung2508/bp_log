package vn.edu.clevai.bplog.service.data.importation.migrate.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import vn.edu.clevai.bplog.dto.cat5.StudentValidDTO;
import vn.edu.clevai.bplog.entity.BpPodProductOfDeal;
import vn.edu.clevai.bplog.entity.BpUsiUserItem;
import vn.edu.clevai.bplog.repository.BpPodProductOfDealRepository;
import vn.edu.clevai.bplog.repository.BpUsiUserItemRepository;
import vn.edu.clevai.bplog.service.data.importation.migrate.Cat5MigrateService;

@Service
@Slf4j
public class Cat5MigrateServiceImpl implements Cat5MigrateService {

	@Autowired
	private BpUsiUserItemRepository userItemRepo;

	@Autowired
	private BpPodProductOfDealRepository podRepo;

	@Override
	@Transactional
	public void doMigrate(String csvFilePath) {
		String line = "";
		String splitBy = ",";
		List<StudentValidDTO> listDto = new ArrayList<StudentValidDTO>();
		try {
			// parsing a CSV file into BufferedReader class constructor
			BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
			int count = 0;
			while ((line = br.readLine()) != null) // returns a Boolean value
			{
				count++;
				if (count > 1) {
					String[] student = line.split(splitBy); // use comma as separator
					StudentValidDTO dto = StudentValidDTO.builder().build();
					dto.setStudentId(Long.valueOf(student[4]));
					dto.setPodId(Long.valueOf(student[0]));
					dto.setUsername(student[1]);
					dto.setFirstname(student[5]);
					dto.setLastname(student[6]);
					dto.setProduct(student[8]);
					listDto.add(dto);
					log.debug("Student [POD Id=" + dto.getPodId() + ", Last Name=" + dto.getLastname() + ", First name="
							+ dto.getFirstname() + ", student id=" + dto.getStudentId() + "]");

				}
			}
			br.close();
		} catch (IOException e) {
			log.error("Error when import user item");
		}
		if (!CollectionUtils.isEmpty(listDto)) {
			doSaveUserItem(listDto);
		}
	}

	private void doSaveUserItem(List<StudentValidDTO> listDto) {
		List<String> listUserCode = listDto.stream().map(k -> k.getUsername()).collect(Collectors.toList());
		List<BpUsiUserItem> listAvaiableUItem = userItemRepo.findAllByCodeIn(listUserCode);
		List<StudentValidDTO> listDtoNew = listDto.stream()
				.filter(k -> !listAvaiableUItem.stream().anyMatch(j -> j.getCode().equalsIgnoreCase(k.getUsername())))
				.collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(listDtoNew)) {
			List<BpUsiUserItem> listItemNew = listDtoNew.stream().map(convertToUserItem()).collect(Collectors.toList());
			userItemRepo.saveAll(listItemNew);
			saveProductOfDeal(listDto, listItemNew);
		}
	}

	private void saveProductOfDeal(List<StudentValidDTO> listDto, List<BpUsiUserItem> listItemNew) {
		List<BpPodProductOfDeal> listPod = new ArrayList<>();
		for (BpUsiUserItem item : listItemNew) {
			StudentValidDTO dto = listDto.stream().filter(k -> k.getUsername().equalsIgnoreCase(item.getCode()))
					.findAny().orElse(null);
			if (Objects.nonNull(dto)) {
				String productName = dto.getProduct();
				String myPt = findMyPT(productName);
				log.info("MyPT is {}", myPt);
				listPod.add(BpPodProductOfDeal.builder().myst(item.getCode()).code(String.valueOf(dto.getPodId()))
						.mypt(myPt).createdAt(new Timestamp(System.currentTimeMillis()))
						.updatedAt(new Timestamp(System.currentTimeMillis())).build());
			} else {
				log.error("Something went wrong. Cant found StudentValidDTO");
			}
		}
		if (!CollectionUtils.isEmpty(listPod)) {
			podRepo.saveAll(listPod);
		}
	}

	private Function<StudentValidDTO, BpUsiUserItem> convertToUserItem() {
		return t -> {
			return BpUsiUserItem.builder().code(t.getUsername()).username(t.getUsername()).myust("ST")
					.firstname(t.getFirstname()).lastname(t.getLastname())
					.createdAt(new Timestamp(System.currentTimeMillis()))
					.updatedAt(new Timestamp(System.currentTimeMillis())).build();
		};
	}

	private String findMyPT(String cats5PT) {
		return CAT5ProductMapping.findCodeByName(cats5PT);
	}

	enum CAT5ProductMapping {
		BASIC_CAPTAIN("Basic Captain- Math", "BC"), TP10("High School TP10- Math", "TP10"), MEDIUM("Medium", "MD"),
		PLUS_OLYMPIA("Plus Olympia- Math", "PM"), PRIVILEGE("Privilege", "PV");

		String name;
		String code;

		CAT5ProductMapping(String name, String code) {
			this.name = name;
			this.code = code;
		}

		public static String findCodeByName(String name) {
			for (CAT5ProductMapping mapping : values()) {
				if (mapping.getName().equalsIgnoreCase(name))
					return mapping.getCode();
			}
			return null;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

	}

	@Override
	@Transactional
	public void doImport(List<StudentValidDTO> listDto) throws Exception {
		if (!CollectionUtils.isEmpty(listDto)) {
			doSaveUserItem(listDto);
		}
	}
}
