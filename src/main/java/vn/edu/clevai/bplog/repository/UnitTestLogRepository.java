package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.clevai.bplog.entity.UnitTestLog;

public interface UnitTestLogRepository extends JpaRepository<UnitTestLog, Integer> {
}
