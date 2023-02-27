package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.costCategory

import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.financialOverview.ReportProjectPartnerExpenditureCostCategoryEntity
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

    @Query(
        """
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
    """
    )
    fun findCumulativeForReportIds(reportIds: Set<Long>): BudgetCostsCalculationResultFull

    @Query(
        """
        SELECT new io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull(
            COALESCE(SUM(report.staffCurrentParked), 0),
            COALESCE(SUM(report.officeCurrentParked), 0),
            COALESCE(SUM(report.travelCurrentParked), 0),
            COALESCE(SUM(report.externalCurrentParked), 0),
            COALESCE(SUM(report.equipmentCurrentParked), 0),
            COALESCE(SUM(report.infrastructureCurrentParked), 0),
            COALESCE(SUM(report.otherCurrentParked), 0),
            COALESCE(SUM(report.lumpSumCurrentParked), 0),
            COALESCE(SUM(report.unitCostCurrentParked), 0),
            COALESCE(SUM(report.sumCurrentParked), 0)
        )
        FROM #{#entityName} report
        WHERE report.reportEntity.id IN :reportIds
    """
    )
    fun findParkedCumulativeForReportIds(reportIds: Set<Long>): BudgetCostsCalculationResultFull

    @Query(
        """
        SELECT new io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull(
            COALESCE(SUM(report.staffTotalEligibleAfterControl), 0),
            COALESCE(SUM(report.officeTotalEligibleAfterControl), 0),
            COALESCE(SUM(report.travelTotalEligibleAfterControl), 0),
            COALESCE(SUM(report.externalTotalEligibleAfterControl), 0),
            COALESCE(SUM(report.equipmentTotalEligibleAfterControl), 0),
            COALESCE(SUM(report.infrastructureTotalEligibleAfterControl), 0),
            COALESCE(SUM(report.otherTotalEligibleAfterControl), 0),
            COALESCE(SUM(report.lumpSumTotalEligibleAfterControl), 0),
            COALESCE(SUM(report.unitCostTotalEligibleAfterControl), 0),
            COALESCE(SUM(report.sumTotalEligibleAfterControl), 0)
        )
        FROM #{#entityName} report
        WHERE report.reportEntity.id IN :reportIds
    """
    )
    fun findCumulativeForReportIdsTotalAfterEligible(reportIds: Set<Long>): BudgetCostsCalculationResultFull
}
