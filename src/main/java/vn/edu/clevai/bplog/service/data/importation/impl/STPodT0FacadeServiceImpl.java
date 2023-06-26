package vn.edu.clevai.bplog.service.data.importation.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.edu.clevai.bplog.payload.request.StudentValidRequest;
import vn.edu.clevai.common.proxy.bplog.payload.response.StudentValidResponse;
import vn.edu.clevai.bplog.service.data.importation.STPodT0FacadeService;

@Service
public class STPodT0FacadeServiceImpl implements STPodT0FacadeService {

	@Override
	public List<Long> exportXDealValidAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StudentValidResponse> getXStudent(List<Long> podIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUSIs(List<StudentValidRequest> listStudents) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setUSI(StudentValidRequest dto) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPOD(StudentValidRequest dto) {
		// TODO Auto-generated method stub
		
	}

}
