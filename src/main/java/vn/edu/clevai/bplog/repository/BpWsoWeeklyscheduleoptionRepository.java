package vn.edu.clevai.bplog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.clevai.bplog.entity.BpWsoWeeklyscheduleoption;

public interface BpWsoWeeklyscheduleoptionRepository extends JpaRepository<BpWsoWeeklyscheduleoption, Integer> {
	Optional<BpWsoWeeklyscheduleoption> findByCode(String code);
}
