package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.costCategory

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectPartnerReportExpenditureCostCategoryPersistenceProvider(
    private val expenditureCostCategoryRepository: ReportProjectPartnerExpenditureCostCategoryRepository,
) : ProjectPartnerReportExpenditureCostCategoryPersistence {

    @Transactional(readOnly = true)
    override fun getCostCategories(partnerId: Long, reportId: Long): ReportExpenditureCostCategory =
        expenditureCostCategoryRepository
            .findFirstByReportEntityPartnerIdAndReportEntityId(partnerId = partnerId, reportId = reportId)
            .toModel()

    @Transactional(readOnly = true)
    override fun getCostCategoriesCumulative(reportIds: Set<Long>) =
        expenditureCostCategoryRepository.findCumulativeForReportIds(reportIds)

    @Transactional
    override fun updateCurrentlyReportedValues(
        partnerId: Long,
        reportId: Long,
        currentlyReported: BudgetCostsCalculationResultFull
    ) {
        expenditureCostCategoryRepository
            .findFirstByReportEntityPartnerIdAndReportEntityId(partnerId = partnerId, reportId = reportId).apply {
                staffCurrent = currentlyReported.staff
                officeCurrent = currentlyReported.office
                travelCurrent = currentlyReported.travel
                externalCurrent = currentlyReported.external
                equipmentCurrent = currentlyReported.equipment
                infrastructureCurrent = currentlyReported.infrastructure
                otherCurrent = currentlyReported.other
                lumpSumCurrent = currentlyReported.lumpSum
                unitCostCurrent = currentlyReported.unitCost
                sumCurrent = currentlyReported.sum
            }
    }

    @Transactional
    override fun updateAfterControlValues(
        partnerId: Long,
        reportId: Long,
        afterControl: BudgetCostsCalculationResultFull
    ) {
        expenditureCostCategoryRepository
            .findFirstByReportEntityPartnerIdAndReportEntityId(partnerId = partnerId, reportId = reportId).apply {
                staffTotalEligibleAfterControl = afterControl.staff
                officeTotalEligibleAfterControl = afterControl.office
                travelTotalEligibleAfterControl = afterControl.travel
                externalTotalEligibleAfterControl = afterControl.external
                equipmentTotalEligibleAfterControl = afterControl.equipment
                infrastructureTotalEligibleAfterControl = afterControl.infrastructure
                otherTotalEligibleAfterControl = afterControl.other
                lumpSumTotalEligibleAfterControl = afterControl.lumpSum
                unitCostTotalEligibleAfterControl = afterControl.unitCost
                sumTotalEligibleAfterControl = afterControl.sum
            }
    }
}
