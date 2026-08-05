package com.skillgap.repository;

import com.skillgap.model.SkillGapReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SkillGapReportRepository extends JpaRepository<SkillGapReport, Long> {
    List<SkillGapReport> findByUserIdOrderByGeneratedAtDesc(Long userId);
    List<SkillGapReport> findByJobRoleId(Long jobRoleId);
}
