package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.costCategory

import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.financialOverview.ReportProjectPartnerExpenditureCostCategoryEntity
import io.cloudflight.jems.server.project.repository.report.partner.model.PerPartnerCertificateCostCategory
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

    @Query("""
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
    """)
    fun findParkedCumulativeForReportIds(reportIds: Set<Long>): BudgetCostsCalculationResultFull

    @Query("""
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
    """)
    fun findCumulativeForReportIdsTotalAfterEligible(reportIds: Set<Long>): BudgetCostsCalculationResultFull

    @Query("""
        SELECT new io.cloudflight.jems.server.project.repository.report.partner.model.PerPartnerCertificateCostCategory(
            report.reportEntity.partnerId,
            report.officeAndAdministrationOnStaffCostsFlatRate,
            report.officeAndAdministrationOnDirectCostsFlatRate,
            report.travelAndAccommodationOnStaffCostsFlatRate,
            report.staffCostsFlatRate,
            report.otherCostsOnStaffCostsFlatRate,
            SUM(report.staffTotalEligibleAfterControl),
            SUM(report.officeTotalEligibleAfterControl),
            SUM(report.travelTotalEligibleAfterControl),
            SUM(report.externalTotalEligibleAfterControl),
            SUM(report.equipmentTotalEligibleAfterControl),
            SUM(report.infrastructureTotalEligibleAfterControl),
            SUM(report.otherTotalEligibleAfterControl),
            SUM(report.lumpSumTotalEligibleAfterControl),
            SUM(report.unitCostTotalEligibleAfterControl),
            SUM(report.sumTotalEligibleAfterControl),
            SUM(report.staffCurrent - report.staffTotalEligibleAfterControl - report.staffCurrentParked),
            SUM(report.officeCurrent - report.officeTotalEligibleAfterControl - report.officeCurrentParked),
            SUM(report.travelCurrent - report.travelTotalEligibleAfterControl - report.travelCurrentParked),
            SUM(report.externalCurrent - report.externalTotalEligibleAfterControl - report.externalCurrentParked),
            SUM(report.equipmentCurrent - report.equipmentTotalEligibleAfterControl - report.equipmentCurrentParked),
            SUM(report.infrastructureCurrent - report.infrastructureTotalEligibleAfterControl - report.infrastructureCurrentParked),
            SUM(report.otherCurrent - report.otherTotalEligibleAfterControl - report.otherCurrentParked),
            SUM(report.lumpSumCurrent - report.lumpSumTotalEligibleAfterControl - report.lumpSumCurrentParked),
            SUM(report.unitCostCurrent - report.unitCostTotalEligibleAfterControl - report.unitCostCurrentParked),
            SUM(report.sumCurrent - report.sumTotalEligibleAfterControl - report.sumCurrentParked)
        )
        FROM #{#entityName} report
        WHERE report.reportEntity.projectReport.id = :projectReportId AND report.reportEntity.projectReport.projectId = :projectId
        GROUP BY report.reportEntity.partnerId
    """)
    fun findPartnerOverviewForProjectReport(projectId: Long, projectReportId: Long): List<PerPartnerCertificateCostCategory>

}
