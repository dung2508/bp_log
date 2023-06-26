package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.clevai.bplog.entity.BPUnitTestLog;

public interface BPUnitTestLogRepository extends JpaRepository<BPUnitTestLog, Long> {
}
