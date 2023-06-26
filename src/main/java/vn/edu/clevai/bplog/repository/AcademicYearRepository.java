package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.clevai.bplog.entity.AcademicYear;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Integer>,
    JpaSpecificationExecutor<AcademicYear> {
}
