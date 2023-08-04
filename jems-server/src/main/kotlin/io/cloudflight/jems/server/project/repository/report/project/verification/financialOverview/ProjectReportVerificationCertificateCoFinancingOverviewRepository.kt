package io.cloudflight.jems.server.project.repository.report.project.verification.financialOverview

import io.cloudflight.jems.server.project.entity.report.verification.financialOverview.ProjectReportVerificationCertificateContributionOverviewEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportVerificationCertificateCoFinancingOverviewRepository :
    JpaRepository<ProjectReportVerificationCertificateContributionOverviewEntity, Long> {

    fun findAllByPartnerReportProjectReportId(projectReportId: Long): List<ProjectReportVerificationCertificateContributionOverviewEntity>

}