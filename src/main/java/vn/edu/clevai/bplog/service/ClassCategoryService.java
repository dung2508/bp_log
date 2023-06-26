package vn.edu.clevai.bplog.service;


import vn.edu.clevai.bplog.entity.ClassCategory;

import java.util.List;

public interface ClassCategoryService {

	ClassCategory findClassCategoryFromBp(String accYear, String mt, String pt, String gg, String wso, String dfdl);

	ClassCategory setClcTed(String accYear, String mt, String pt, String gg, String wso,
							String dfdl, String dfge, Boolean published);

	ClassCategory findClcCsh(String accYear, String gg, String mt, Boolean published);

	ClassCategory createOrUpdate(ClassCategory classCategory);

	List<ClassCategory> findClcCsh(String accYear, String mt, Boolean published);
}
