package io.cloudflight.jems.server.project.repository.report.project.financialOverview.coFinancing

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateCoFinancingEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ReportProjectCertificateCoFinancingRepository :
    JpaRepository<ReportProjectCertificateCoFinancingEntity, ProjectReportEntity> {

    fun findFirstByReportEntityProjectIdAndReportEntityId(
        projectId: Long,
        reportId: Long,
    ): ReportProjectCertificateCoFinancingEntity

    @Query("""
        SELECT new io.cloudflight.jems.server.project.repository.report.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumnWithoutFunds(
            COALESCE(SUM(report.partnerContributionCurrent), 0),
            COALESCE(SUM(report.publicContributionCurrent), 0),
            COALESCE(SUM(report.automaticPublicContributionCurrent), 0),
            COALESCE(SUM(report.privateContributionCurrent), 0),
            COALESCE(SUM(report.sumCurrent), 0)
        )
        FROM #{#entityName} report
        WHERE report.reportEntity.id IN :reportIds
    """)
    fun findCumulativeCurrentForReportIds(reportIds: Set<Long>): ReportCertificateCoFinancingColumnWithoutFunds

    @Query("""
        SELECT new io.cloudflight.jems.server.project.repository.report.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumnWithoutFunds(
            COALESCE(SUM(report.partnerContributionCurrentVerified), 0),
            COALESCE(SUM(report.publicContributionCurrentVerified), 0),
            COALESCE(SUM(report.automaticPublicContributionCurrentVerified), 0),
            COALESCE(SUM(report.privateContributionCurrentVerified), 0),
            COALESCE(SUM(report.sumCurrentVerified), 0)
        )
        FROM #{#entityName} report
        WHERE report.reportEntity.id IN :reportIds
    """)
    fun findCumulativeVerifiedForReportIds(reportIds: Set<Long>): ReportCertificateCoFinancingColumnWithoutFunds

}
