package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.clevai.bplog.entity.Term;

import java.util.List;


public interface TermRepository extends JpaRepository<Term, Long>,
		JpaSpecificationExecutor<Term> {
	List<Term> findTrimestersByNameAndCode(String name, String code);

}
