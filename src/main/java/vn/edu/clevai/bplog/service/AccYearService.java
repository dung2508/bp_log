package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.AccYear;

import java.util.Date;

public interface AccYearService {

	AccYear findByTime(Date input);

}
