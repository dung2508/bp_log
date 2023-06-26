package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.AccYear;

import java.util.Date;
import java.util.Optional;

public interface AccYearRepository extends JpaRepository<AccYear, Long> {

	@Query(value = "" +
			"select *  " +
			"from bp_accyear  " +
			"where DATE(:date) BETWEEN startdate and enddate and published = 1 " +
			" limit 1", nativeQuery = true)
	Optional<AccYear> findByTime(Date date);

}
