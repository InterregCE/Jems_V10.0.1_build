package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.costCategory

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCurrentValuesWrapper
import io.cloudflight.jems.server.project.service.budget.model.ExpenditureCostCategoryCurrentlyReportedWithReIncluded
import io.cloudflight.jems.server.project.service.budget.model.ExpenditureCostCategoryPreviouslyReportedWithParked
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

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
    override fun getCostCategoriesFor(reportIds: Set<Long>): Map<Long, ReportExpenditureCostCategory> =
        expenditureCostCategoryRepository.findAllByReportEntityIdIn(reportIds).associateBy { it.reportEntity.id }
            .mapValues { it.value.toModel() }

    @Transactional(readOnly = true)
    override fun getCostCategoriesCumulative(reportIds: Set<Long>, finalizedReportIds: Set<Long>) = ExpenditureCostCategoryPreviouslyReportedWithParked(
        previouslyReported = expenditureCostCategoryRepository.findCumulativeForReportIds(reportIds),
        previouslyReportedParked = expenditureCostCategoryRepository.findParkedCumulativeForReportIds(reportIds),
        previouslyValidated = expenditureCostCategoryRepository.findCumulativeTotalsForReportIds(finalizedReportIds)
    )

    @Transactional(readOnly = true)
    override fun getVerificationCostCategoriesCumulative(partnerId: Long, finalizedProjectrepotIds: Set<Long>)=
        expenditureCostCategoryRepository.findVerificationParkedCumulativeForProjectReportIds(partnerId, finalizedProjectrepotIds)

    @Transactional
    override fun updateCurrentlyReportedValues(
        partnerId: Long,
        reportId: Long,
        currentlyReportedWithReIncluded: ExpenditureCostCategoryCurrentlyReportedWithReIncluded
    ) {
        expenditureCostCategoryRepository
            .findFirstByReportEntityPartnerIdAndReportEntityId(partnerId = partnerId, reportId = reportId).apply {
                staffCurrent = currentlyReportedWithReIncluded.currentlyReported.staff
                officeCurrent = currentlyReportedWithReIncluded.currentlyReported.office
                travelCurrent = currentlyReportedWithReIncluded.currentlyReported.travel
                externalCurrent = currentlyReportedWithReIncluded.currentlyReported.external
                equipmentCurrent = currentlyReportedWithReIncluded.currentlyReported.equipment
                infrastructureCurrent = currentlyReportedWithReIncluded.currentlyReported.infrastructure
                otherCurrent = currentlyReportedWithReIncluded.currentlyReported.other
                lumpSumCurrent = currentlyReportedWithReIncluded.currentlyReported.lumpSum
                unitCostCurrent = currentlyReportedWithReIncluded.currentlyReported.unitCost
                spfCostCurrent = currentlyReportedWithReIncluded.currentlyReported.spfCost
                sumCurrent = currentlyReportedWithReIncluded.currentlyReported.sum

                staffCurrentReIncluded = currentlyReportedWithReIncluded.currentlyReportedReIncluded.staff
                officeCurrentReIncluded = currentlyReportedWithReIncluded.currentlyReportedReIncluded.office
                travelCurrentReIncluded = currentlyReportedWithReIncluded.currentlyReportedReIncluded.travel
                externalCurrentReIncluded = currentlyReportedWithReIncluded.currentlyReportedReIncluded.external
                equipmentCurrentReIncluded = currentlyReportedWithReIncluded.currentlyReportedReIncluded.equipment
                infrastructureCurrentReIncluded = currentlyReportedWithReIncluded.currentlyReportedReIncluded.infrastructure
                otherCurrentReIncluded = currentlyReportedWithReIncluded.currentlyReportedReIncluded.other
                lumpSumCurrentReIncluded = currentlyReportedWithReIncluded.currentlyReportedReIncluded.lumpSum
                unitCostCurrentReIncluded = currentlyReportedWithReIncluded.currentlyReportedReIncluded.unitCost
                spfCostCurrentReIncluded = currentlyReportedWithReIncluded.currentlyReportedReIncluded.spfCost
                sumCurrentReIncluded = currentlyReportedWithReIncluded.currentlyReportedReIncluded.sum
            }
    }

    @Transactional
    override fun updateAfterControlValues(
        partnerId: Long,
        reportId: Long,
        afterControlWithParked: BudgetCostsCurrentValuesWrapper
    ) {
        expenditureCostCategoryRepository
            .findFirstByReportEntityPartnerIdAndReportEntityId(partnerId = partnerId, reportId = reportId).apply {
                staffTotalEligibleAfterControl = afterControlWithParked.currentlyReported.staff
                officeTotalEligibleAfterControl = afterControlWithParked.currentlyReported.office
                travelTotalEligibleAfterControl = afterControlWithParked.currentlyReported.travel
                externalTotalEligibleAfterControl = afterControlWithParked.currentlyReported.external
                equipmentTotalEligibleAfterControl = afterControlWithParked.currentlyReported.equipment
                infrastructureTotalEligibleAfterControl = afterControlWithParked.currentlyReported.infrastructure
                otherTotalEligibleAfterControl = afterControlWithParked.currentlyReported.other
                lumpSumTotalEligibleAfterControl = afterControlWithParked.currentlyReported.lumpSum
                unitCostTotalEligibleAfterControl = afterControlWithParked.currentlyReported.unitCost
                sumTotalEligibleAfterControl = afterControlWithParked.currentlyReported.sum

                staffCurrentParked = afterControlWithParked.currentlyReportedParked.staff
                officeCurrentParked = afterControlWithParked.currentlyReportedParked.office
                travelCurrentParked = afterControlWithParked.currentlyReportedParked.travel
                externalCurrentParked = afterControlWithParked.currentlyReportedParked.external
                equipmentCurrentParked = afterControlWithParked.currentlyReportedParked.equipment
                infrastructureCurrentParked = afterControlWithParked.currentlyReportedParked.infrastructure
                otherCurrentParked = afterControlWithParked.currentlyReportedParked.other
                lumpSumCurrentParked = afterControlWithParked.currentlyReportedParked.lumpSum
                unitCostCurrentParked = afterControlWithParked.currentlyReportedParked.unitCost
                sumCurrentParked = afterControlWithParked.currentlyReportedParked.sum
            }
    }

    @Transactional
    override fun updateAfterVerificationParkedValues(parkedValuesPerCertificate: Map<Long, BudgetCostsCalculationResultFull>) {
        expenditureCostCategoryRepository.findAllByReportEntityIdIn(parkedValuesPerCertificate.keys).forEach {
            val parkedAfterVerification = parkedValuesPerCertificate[it.reportId]
            it.apply {
                staffCurrentParkedVerification = parkedAfterVerification?.staff ?: BigDecimal.ZERO
                officeCurrentParkedVerification = parkedAfterVerification?.office ?: BigDecimal.ZERO
                travelCurrentParkedVerification = parkedAfterVerification?.travel ?: BigDecimal.ZERO
                externalCurrentParkedVerification = parkedAfterVerification?.external ?: BigDecimal.ZERO
                equipmentCurrentParkedVerification = parkedAfterVerification?.equipment ?: BigDecimal.ZERO
                infrastructureCurrentParkedVerification = parkedAfterVerification?.infrastructure ?: BigDecimal.ZERO
                otherCurrentParkedVerification = parkedAfterVerification?.other ?: BigDecimal.ZERO
                lumpSumCurrentParkedVerification = parkedAfterVerification?.lumpSum ?: BigDecimal.ZERO
                unitCostCurrentParkedVerification = parkedAfterVerification?.unitCost ?: BigDecimal.ZERO
                spfCostCurrentParkedVerification = parkedAfterVerification?.spfCost ?: BigDecimal.ZERO
                sumCurrentParkedVerification = parkedAfterVerification?.sum ?: BigDecimal.ZERO
            }
        }
    }

    @Transactional(readOnly = true)
    override fun getCostCategoriesTotalEligible(reportIds: Set<Long>): BudgetCostsCalculationResultFull {
        return expenditureCostCategoryRepository.findCumulativeForReportIdsTotalAfterEligible(reportIds)
    }
}
