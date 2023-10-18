package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlDeductionOverview

import io.cloudflight.jems.server.programme.service.typologyerrors.ProgrammeTypologyErrorsPersistence
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverview
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverviewRow
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview.calculateCertified
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview.onlyParkedOnes
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.calculateCurrent
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetReportControlDeductionOverviewCalculator(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val typologyOfErrorsPersistence: ProgrammeTypologyErrorsPersistence,
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence,
    private val reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence,
) {

    @Transactional(readOnly = true)
    fun get(partnerId: Long, reportId: Long): ControlDeductionOverview {
        val isClosed = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId).status.isFinalized()

        val expenditures = reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(partnerId, reportId)
        val costCategories = reportExpenditureCostCategoryPersistence.getCostCategories(partnerId = partnerId, reportId)
        val options = costCategories.options

        val totalDeclared = costCategories.currentlyReported
        val totalParked = if (isClosed) costCategories.currentlyReportedParked else expenditures.onlyParkedOnes().calculateCurrent(options)
        val totalEligibleAfterControl = if (isClosed) costCategories.totalEligibleAfterControl else expenditures.calculateCertified(options)

        val totalDeductedSplit = totalDeclared.minus(totalParked).minus(totalEligibleAfterControl)

        val byTypologyError = expenditures
            .filter { it.typologyOfErrorId != null }
            .groupBy { it.typologyOfErrorId!! }
            .mapValues {
                it.value
                    .groupBy { it.getCategory() }
                    .mapValues { it.value.sumOf { it.deductedAmount } }
            }

        val result = this.typologyOfErrorsPersistence.getAllTypologyErrors()
            .map { error -> byTypologyError.extractFor(error = error) }
            .plus(options.toRow(totalDeductedSplit))
            .fillInTotal()

        return ControlDeductionOverview(
            deductionRows = result,
            staffCostsFlatRate = options.staffCostsFlatRate,
            officeAndAdministrationFlatRate = options.officeAndAdministrationOnStaffCostsFlatRate
                ?: options.officeAndAdministrationOnDirectCostsFlatRate,
            travelAndAccommodationFlatRate = options.travelAndAccommodationOnStaffCostsFlatRate,
            otherCostsOnStaffCostsFlatRate = options.otherCostsOnStaffCostsFlatRate,
            total = result.sumUp(),
        )
    }


    private fun Map<Long, Map<BudgetCostCategory, BigDecimal>>.getForTypologyAndCategory(typologyId: Long, category: BudgetCostCategory): BigDecimal? =
        get(typologyId)?.get(category) ?: BigDecimal.ZERO

    private fun List<ControlDeductionOverviewRow>.fillInTotal() = this.onEach {
        it.total = (it.staffCost ?: BigDecimal.ZERO)
            .plus(it.officeAndAdministration ?: BigDecimal.ZERO)
            .plus(it.travelAndAccommodation ?: BigDecimal.ZERO)
            .plus(it.externalExpertise ?: BigDecimal.ZERO)
            .plus(it.equipment ?: BigDecimal.ZERO)
            .plus(it.infrastructureAndWorks ?: BigDecimal.ZERO)
            .plus(it.lumpSums ?: BigDecimal.ZERO)
            .plus(it.unitCosts ?: BigDecimal.ZERO)
            .plus(it.otherCosts ?: BigDecimal.ZERO)
    }

    private fun ProjectPartnerBudgetOptions.toRow(flatRates: BudgetCostsCalculationResultFull) = ControlDeductionOverviewRow(
        typologyOfErrorId = null,
        typologyOfErrorName = null,
        staffCost = if (staffCostsFlatRate == null) null else flatRates.staff,
        officeAndAdministration = if (officeAndAdministrationOnDirectCostsFlatRate == null
            && officeAndAdministrationOnStaffCostsFlatRate == null) null else flatRates.office,
        travelAndAccommodation = if (travelAndAccommodationOnStaffCostsFlatRate == null) null else flatRates.travel,
        externalExpertise = null,
        equipment = null,
        infrastructureAndWorks = null,
        lumpSums = null,
        unitCosts = null,
        otherCosts = if (otherCostsOnStaffCostsFlatRate == null) null else flatRates.other
    )

    private fun Map<Long, Map<BudgetCostCategory, BigDecimal>>.extractFor(error: TypologyErrors) = ControlDeductionOverviewRow(
        typologyOfErrorId = error.id,
        typologyOfErrorName = error.description,
        staffCost = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.Staff),
        officeAndAdministration = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.Office),
        travelAndAccommodation = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.Travel),
        externalExpertise = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.External),
        equipment = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.Equipment),
        infrastructureAndWorks = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.Infrastructure),
        lumpSums = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.LumpSum),
        unitCosts = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.UnitCost),
        otherCosts = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.Other),
    )

    private fun BudgetCostsCalculationResultFull.minus(subtractor: BudgetCostsCalculationResultFull) = BudgetCostsCalculationResultFull(
        staff = staff.minus(subtractor.staff),
        office = office.minus(subtractor.office),
        travel = travel.minus(subtractor.travel),
        external = external.minus(subtractor.external),
        equipment = equipment.minus(subtractor.equipment),
        infrastructure = infrastructure.minus(subtractor.infrastructure),
        other = other.minus(subtractor.other),
        lumpSum = lumpSum.minus(subtractor.lumpSum),
        unitCost = unitCost.minus(subtractor.unitCost),
        spfCost = spfCost.minus(subtractor.spfCost),
        sum = sum.minus(subtractor.sum),
    )
}
