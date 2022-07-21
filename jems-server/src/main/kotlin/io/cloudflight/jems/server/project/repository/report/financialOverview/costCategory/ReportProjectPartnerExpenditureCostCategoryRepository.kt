package io.cloudflight.jems.server.project.repository.report.financialOverview.costCategory

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.financialOverview.ReportProjectPartnerExpenditureCostCategoryEntity
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ReportProjectPartnerExpenditureCostCategoryRepository :
    JpaRepository<ReportProjectPartnerExpenditureCostCategoryEntity, ProjectPartnerReportEntity> {

    fun findFirstByReportEntityPartnerIdAndReportEntityId(
        partnerId: Long,
        reportId: Long,
    ): ReportProjectPartnerExpenditureCostCategoryEntity

    @Query("""
        SELECT new io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull(
            COALESCE(SUM(report.staffCurrent), 0),
            COALESCE(SUM(report.officeCurrent), 0),
            COALESCE(SUM(report.travelCurrent), 0),
            COALESCE(SUM(report.externalCurrent), 0),
            COALESCE(SUM(report.equipmentCurrent), 0),
            COALESCE(SUM(report.infrastructureCurrent), 0),
            COALESCE(SUM(report.otherCurrent), 0),
            COALESCE(SUM(report.lumpSumCurrent), 0),
            COALESCE(SUM(report.unitCostCurrent), 0),
            COALESCE(SUM(report.sumCurrent), 0)
        )
        FROM #{#entityName} report
        WHERE report.reportEntity.id IN :reportIds
    """)
    fun findCumulativeForReportIds(reportIds: Set<Long>): BudgetCostsCalculationResultFull

}
