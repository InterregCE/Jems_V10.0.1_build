package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.coFinancing

import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.financialOverview.ReportProjectPartnerExpenditureCoFinancingEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.Optional

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
        SELECT SUM(rppecf.sumTotalEligibleAfterControl)
        FROM #{#entityName} rppecf
        WHERE rppecf.reportId IN (
            SELECT rpp.id FROM report_project_partner rpp
            INNER JOIN report_project rp
            ON rpp.projectReport.id = rp.id
            WHERE rp.status = 'Submitted' AND rpp.partnerId = :partnerId
        )
    """)
    fun getTotalEligibleSumForSubmittedReports(partnerId: Long): Optional<BigDecimal>
}
