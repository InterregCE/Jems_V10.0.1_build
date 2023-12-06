package io.cloudflight.jems.server.project.repository.report.project.verification.financialOverview

import io.cloudflight.jems.server.project.entity.report.verification.financialOverview.ProjectReportVerificationCertificateSpfContributionOverviewEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportVerificationCertificateSpfCoFinancingOverviewRepository :
    JpaRepository<ProjectReportVerificationCertificateSpfContributionOverviewEntity, Long> {

    fun findAllByProjectReportId(projectReportId: Long): List<ProjectReportVerificationCertificateSpfContributionOverviewEntity>

    fun deleteAllByProjectReportId(projectReportId: Long)

}
