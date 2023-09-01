package io.cloudflight.jems.server.project.repository.report.project.verification

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportVerificationClarificationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportVerificationClarificationRepository :
    JpaRepository<ProjectReportVerificationClarificationEntity, Long> {
        fun findByProjectReportIdOrderByNumber(reportId: Long): List<ProjectReportVerificationClarificationEntity>
}