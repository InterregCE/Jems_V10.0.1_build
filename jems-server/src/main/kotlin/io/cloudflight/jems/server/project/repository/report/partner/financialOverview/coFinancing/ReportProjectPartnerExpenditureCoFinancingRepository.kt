package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.coFinancing

import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.financialOverview.ReportProjectPartnerExpenditureCoFinancingEntity
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
        SELECT new io.cloudflight.jems.server.project.repository.report.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumnWithoutFunds(
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

    @Query("""
        SELECT new io.cloudflight.jems.server.project.repository.report.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumnWithoutFunds(
            COALESCE(SUM(report.partnerContributionCurrentParked), 0),
            COALESCE(SUM(report.publicContributionCurrentParked), 0),
            COALESCE(SUM(report.automaticPublicContributionCurrentParked), 0),
            COALESCE(SUM(report.privateContributionCurrentParked), 0),
            COALESCE(SUM(report.sumCurrentParked), 0)
        )
        FROM #{#entityName} report
        WHERE report.reportEntity.id IN :reportIds
    """)
    fun findCumulativeParkedForReportIds(reportIds: Set<Long>): ReportExpenditureCoFinancingColumnWithoutFunds

    @Query("""
        SELECT new io.cloudflight.jems.server.project.repository.report.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumnWithoutFunds(
            COALESCE(SUM(report.partnerContributionTotalEligibleAfterControl), 0),
            COALESCE(SUM(report.publicContributionTotalEligibleAfterControl), 0),
            COALESCE(SUM(report.automaticPublicContributionTotalEligibleAfterControl), 0),
            COALESCE(SUM(report.privateContributionTotalEligibleAfterControl), 0),
            COALESCE(SUM(report.sumTotalEligibleAfterControl), 0)
        )
        FROM #{#entityName} report
        WHERE report.reportEntity.id IN :reportIds
    """)
    fun findCumulativeTotalsForReportIds(reportIds: Set<Long>): ReportExpenditureCoFinancingColumnWithoutFunds

}
