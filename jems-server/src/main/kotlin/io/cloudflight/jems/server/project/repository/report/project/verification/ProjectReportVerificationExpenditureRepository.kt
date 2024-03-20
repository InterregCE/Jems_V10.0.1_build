package io.cloudflight.jems.server.project.repository.report.project.verification

import io.cloudflight.jems.server.project.entity.report.verification.expenditure.ProjectReportVerificationExpenditureEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportVerificationExpenditureRepository:
    JpaRepository<ProjectReportVerificationExpenditureEntity, Long> {
        fun findAllByExpenditurePartnerReportProjectReportId(projectReportId: Long): List<ProjectReportVerificationExpenditureEntity>

        fun findAllByExpenditurePartnerReportProjectReportIdAndParkedIsTrue(projectReportId: Long): List<ProjectReportVerificationExpenditureEntity>

        fun deleteAllByExpenditurePartnerReportProjectReportIdIsNull()
}
