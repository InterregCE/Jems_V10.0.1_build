package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureCostAfterControl
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.ExpenditureIdentifiers
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.ExpenditureVerification
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.calculateCostCategoriesFor


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
    costCategory = expenditure.costCategory,
    declaredAmountAfterSubmission = expenditure.declaredAmountAfterSubmission,
    parkingMetadata = expenditure.parkingMetadata,
    partOfSample = partOfVerificationSample,
    amountAfterVerification = amountAfterVerification,
    certifiedAmount = expenditure.certifiedAmount,
    parked = parked,
    deductedByJs = deductedByJs,
    deductedByMa = deductedByMa,
    typologyOfErrorId = typologyOfErrorId
)