package io.cloudflight.jems.server.project.service.report.project.verification

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureCostAfterControl
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.ExpenditureIdentifiers
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.ExpenditureVerification
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.calculateCostCategoriesFor
import java.math.BigDecimal


fun Collection<ExpenditureVerification>.calculateVerified(options: ProjectPartnerBudgetOptions) =
    calculateCostCategoriesFor(options) { it.amountAfterVerification }

fun Collection<ExpenditureCostAfterControl>.calculateCertified(
    options: ProjectPartnerBudgetOptions
): BudgetCostsCalculationResultFull =
    calculateCostCategoriesFor(options) { it.certifiedAmount }


fun List<ExpenditureVerification>.calculateTotalDeductedPerCostCategory() =
    this.groupBy { it.getCategory() }
        .mapValues {
            it.value.sumOf { verificationExpenditure ->
                verificationExpenditure.certifiedAmount.minus(verificationExpenditure.amountAfterVerification)
            }
        }

fun Collection<ExpenditureVerification>.onlyParkedOnes() =
    filter { it.parked }


fun Map<ExpenditureIdentifiers, List<ExpenditureVerification>>.calculateCostCategoriesCurrentVerified(
    budgetOptionsByCertificate: Map<Long, ProjectPartnerBudgetOptions>
): BudgetCostsCalculationResultFull {

    var sum = BudgetCostsCalculationResultFull(
        staff = BigDecimal.ZERO,
        office = BigDecimal.ZERO,
        travel = BigDecimal.ZERO,
        external = BigDecimal.ZERO,
        equipment = BigDecimal.ZERO,
        infrastructure = BigDecimal.ZERO,
        other = BigDecimal.ZERO,
        lumpSum = BigDecimal.ZERO,
        unitCost = BigDecimal.ZERO,
        sum = BigDecimal.ZERO,
    )
    this.forEach{ (certificate,expenditures) ->
        val certificateOptions = budgetOptionsByCertificate[certificate.partnerReportId]
        sum = sum.plus(expenditures.calculateVerified(certificateOptions!!))
    }

    return sum
}


fun ProjectReportVerificationExpenditureLine.toIdentifiers() = ExpenditureIdentifiers(
    partnerId = expenditure.partnerId,
    partnerRole = expenditure.partnerRole,
    partnerNumber = expenditure.partnerNumber,
    partnerReportId = expenditure.partnerReportId,
    partnerReportNumber = expenditure.partnerReportNumber,
)

fun ProjectReportVerificationExpenditureLine.toVerification() = ExpenditureVerification(
    id = expenditure.id,
    lumpSumId = expenditure.lumpSum?.lumpSumProgrammeId,
    unitCostId = expenditure.unitCost?.id,
    investmentId = expenditure.investment?.id,
    costCategory = expenditure.costCategory,
    declaredAmountAfterSubmission = expenditure.declaredAmountAfterSubmission,
    parkingMetadata = expenditure.parkingMetadata,
    partOfSample = partOfVerificationSample,
    amountAfterVerification = amountAfterVerification,
    certifiedAmount = expenditure.certifiedAmount,
    parked = parked,
    deductedByJs = deductedByJs,
    deductedByMa = deductedByMa,
)

private fun BudgetCostsCalculationResultFull.plus(adder: BudgetCostsCalculationResultFull) =
    BudgetCostsCalculationResultFull(
        staff = staff.plus(adder.staff),
        office = office.plus(adder.office),
        travel = travel.plus(adder.travel),
        external = external.plus(adder.external),
        equipment = equipment.plus(adder.equipment),
        infrastructure = infrastructure.plus(adder.infrastructure),
        other = other.plus(adder.other),
        lumpSum = lumpSum.plus(adder.lumpSum),
        unitCost = unitCost.plus(adder.unitCost),
        sum = sum.plus(adder.sum),
    )
