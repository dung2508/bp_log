package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.dto.bp.PtGgDfdlDTO;
import vn.edu.clevai.bplog.entity.BpPTProductType;
import java.util.List;

public interface BpPTProductTypeService {
	List<BpPTProductType> findAllByCode(List<String> codes);

	List<BpPTProductType> findAll();

	PtGgDfdlDTO findGGDFDLByProductCode(String capCode, String code, String register);

	PtGgDfdlDTO findGGByPT(String pt, String register);


}
