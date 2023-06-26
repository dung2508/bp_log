package vn.edu.clevai.bplog.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.entity.ClassCategory;
import vn.edu.clevai.bplog.repository.ClassCategoryRepository;
import vn.edu.clevai.bplog.service.ClassCategoryService;
import vn.edu.clevai.common.api.exception.NotFoundException;

import java.util.List;
import java.util.Objects;

@Service
public class ClassCategoryServiceImpl implements ClassCategoryService {
	private final ClassCategoryRepository classCategoryRepository;

	public ClassCategoryServiceImpl(ClassCategoryRepository classCategoryRepository) {
		this.classCategoryRepository = classCategoryRepository;
	}


	@Override
	public ClassCategory findClassCategoryFromBp(String accYear, String mt, String pt, String gg, String wso, String dfdl) {
		return classCategoryRepository.findFirstByMyAccYearAndMyPtAndMyGgAndMyDfdlAndMyTermAndPublishedAndMyWso(
				accYear, pt, gg, dfdl, mt, true, wso
		).orElseThrow(
				() -> new NotFoundException("Coun't find cashSta")
		);
	}

	@Override
	public ClassCategory findClcCsh(String accYear, String gg, String mt, Boolean published) {
		return classCategoryRepository.findFirstByMyAccYearAndMyGgAndMyTermAndClcTypeAndPublished(
				accYear, gg, mt, "CLC-SHS", true
		).orElse(null);
	}

	@Override
	public ClassCategory setClcTed(
			String accYear, String mt, String pt, String gg, String wso, String dfdl, String dfge, Boolean published) {
		ClassCategory classCategoryCSH = findClcCsh(accYear, gg, mt, true);
		return classCategoryRepository.findClc(accYear, mt, pt, gg, wso, dfdl, dfge, published, "CLC-TED")
				.orElseGet(() -> classCategoryRepository.save(ClassCategory.builder()
						.clcType("CLC-TED")
						.myAccYear(accYear)
						.myTerm(mt)
						.myPt(pt)
						.myGg(gg)
						.myWso(wso)
						.myDfdl(dfdl)
						.myDfge(dfge)
						.code(getClcCode(accYear, mt, pt, gg, wso, dfdl, dfge, classCategoryCSH.getMyCashSta(), "CLC-TED"))
						.myCashSta(classCategoryCSH.getMyCashSta())
						.published(true)
						.build()));
	}

	public String getClcCode(String accYear, String mt, String pt, String gg, String wso, String dfdl, String dfge,
							 String cashSta, String clcType) {
		return String.join("",
				Objects.isNull(gg) ? "" : gg + "-",
				Objects.isNull(accYear) ? "" : accYear + "-",
				Objects.isNull(mt) ? "" : mt + "-",
				Objects.isNull(pt) ? "" : pt + "-",
				Objects.isNull(cashSta) ? "" : cashSta + "-",
				Objects.isNull(wso) ? "" : wso + "-",
				Objects.isNull(dfdl) ? "" : dfdl + "-",
				Objects.isNull(dfge) ? "" : dfge + "-",
				Objects.isNull(clcType) ? "" : clcType);
	}

	@Override
	public ClassCategory createOrUpdate(ClassCategory classCategoryReq) {
		String clcCode = getClcCode(classCategoryReq.getMyAccYear(), classCategoryReq.getMyTerm(), classCategoryReq.getMyPt(), classCategoryReq.getMyGg(), classCategoryReq.getMyWso(), classCategoryReq.getMyDfdl(), null,
				classCategoryReq.getMyCashSta(), classCategoryReq.getClcType());
		ClassCategory classCategory = classCategoryRepository.findFirstByCode(clcCode).orElseGet(() -> ClassCategory.builder().code(clcCode).build());
		classCategory.setMyAccYear(classCategoryReq.getMyAccYear());
		classCategory.setMyTerm(classCategoryReq.getMyTerm());
		classCategory.setMyPt(classCategoryReq.getMyPt());
		classCategory.setMyGg(classCategoryReq.getMyGg());
		classCategory.setMyWso(classCategoryReq.getMyWso());
		classCategory.setMyDfdl(classCategoryReq.getMyDfdl());
		classCategory.setMyCashSta(classCategoryReq.getMyCashSta());
		classCategory.setClcType(classCategoryReq.getClcType());
		return classCategoryRepository.save(classCategory);


	}

	@Override
	public List<ClassCategory> findClcCsh(String accYear, String mt, Boolean published) {
		return classCategoryRepository.findAllByMyAccYearAndMyTermAndPublished(accYear, mt, published);
	}
}
