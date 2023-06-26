package vn.edu.clevai.bplog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.clevai.bplog.entity.BpCalShiftStart;

public interface BpCalShiftStartRepository extends JpaRepository<BpCalShiftStart, Integer>{
	List<BpCalShiftStart> findAllByCode(String code);
}
