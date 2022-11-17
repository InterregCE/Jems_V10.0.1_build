package io.cloudflight.jems.server.project.repository.report.financialOverview.coFinancing

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.financialOverview.ReportProjectPartnerExpenditureCoFinancingEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ReportProjectPartnerExpenditureCoFinancingRepository :
    JpaRepository<ReportProjectPartnerExpenditureCoFinancingEntity, ProjectPartnerReportEntity> {

    fun findFirstByReportEntityPartnerIdAndReportEntityId(
        partnerId: Long,
        reportId: Long,
    ): ReportProjectPartnerExpenditureCoFinancingEntity

    @Query("""
        SELECT new io.cloudflight.jems.server.project.repository.report.financialOverview.coFinancing.ReportExpenditureCoFinancingColumnWithoutFunds(
            COALESCE(SUM(report.partnerContributionCurrent), 0),
            COALESCE(SUM(report.publicContributionCurrent), 0),
            COALESCE(SUM(report.automaticPublicContributionCurrent), 0),
            COALESCE(SUM(report.privateContributionCurrent), 0),
            COALESCE(SUM(report.sumCurrent), 0)
        )
        FROM #{#entityName} report
        WHERE report.reportEntity.id IN :reportIds
    """)
    fun findCumulativeForReportIds(reportIds: Set<Long>): ReportExpenditureCoFinancingColumnWithoutFunds

}
