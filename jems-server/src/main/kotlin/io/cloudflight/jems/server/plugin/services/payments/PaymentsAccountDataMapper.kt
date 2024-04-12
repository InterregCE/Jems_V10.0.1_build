package io.cloudflight.jems.server.plugin.services.payments

import io.cloudflight.jems.plugin.contract.models.payments.account.AmountWithdrawnPerPriorityData
import io.cloudflight.jems.plugin.contract.models.payments.account.AmountWithdrawnPerYearData
import io.cloudflight.jems.plugin.contract.models.payments.account.PaymentAccountAmountSummaryData
import io.cloudflight.jems.plugin.contract.models.payments.account.PaymentAccountAmountSummaryLineData
import io.cloudflight.jems.plugin.contract.models.payments.account.PaymentAccountCorrectionLinkingData
import io.cloudflight.jems.plugin.contract.models.payments.account.PaymentAccountData
import io.cloudflight.jems.plugin.contract.models.payments.account.PaymentAccountOverviewData
import io.cloudflight.jems.plugin.contract.models.payments.account.PaymentAccountOverviewDetailData
import io.cloudflight.jems.plugin.contract.models.payments.account.PaymentAccountStatusData
import io.cloudflight.jems.plugin.contract.models.payments.account.ReconciledAmountByTypeData
import io.cloudflight.jems.plugin.contract.models.payments.account.ReconciledAmountPerPriorityData
import io.cloudflight.jems.plugin.contract.models.payments.export.ControllingBodyData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.ProjectCorrectionProgrammeMeasureScenarioData
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverview
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverviewDetail
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummary
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummaryLine
import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionLinking
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountByType
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountPerPriority
import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.AmountWithdrawnPerPriority
import io.cloudflight.jems.server.payments.model.account.finance.withdrawn.AmountWithdrawnPerYear
import io.cloudflight.jems.server.plugin.services.toDataModel
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody

fun PaymentAccountCorrectionLinking.toDataModel() = PaymentAccountCorrectionLinkingData(
    correction = correction.toDataModel(),
    projectId = projectId,
    projectAcronym = projectAcronym,
    projectCustomIdentifier = projectCustomIdentifier,
    priorityAxis = priorityAxis,
    controllingBody = controllingBody.toDataModel(),
    scenario = scenario.toDataModel(),
    paymentAccountId = paymentAccountId,
    fundAmount = fundAmount,
    partnerContribution = partnerContribution,
    publicContribution = publicContribution,
    correctedPublicContribution = correctedPublicContribution,
    autoPublicContribution = autoPublicContribution,
    correctedAutoPublicContribution = correctedAutoPublicContribution,
    privateContribution = privateContribution,
    correctedPrivateContribution = correctedPrivateContribution,
    comment = comment
)


fun ControllingBody.toDataModel() =  ControllingBodyData.valueOf(this.name)


fun ProjectCorrectionProgrammeMeasureScenario.toDataModel() = ProjectCorrectionProgrammeMeasureScenarioData.valueOf(this.name)

fun List<PaymentAccountOverview>.toDataModel() = map { it.toDataModel() }

fun PaymentAccountOverview.toDataModel() = PaymentAccountOverviewData(
    programmeFund = programmeFund.toDataModel(),
    paymentAccounts = paymentAccounts.map { it.toDataModel() }
)

fun PaymentAccountOverviewDetail.toDataModel() = PaymentAccountOverviewDetailData(
    id = id,
    accountingYear = accountingYear.toDataModel(),
    status = status.toDataModel(),
    totalEligibleExpenditure = totalEligibleExpenditure,
    nationalReference = nationalReference,
    technicalAssistance = technicalAssistance,
    totalPublicContribution = totalPublicContribution,
    totalClaimInclTA = totalClaimInclTA,
    submissionToSfcDate = submissionToSfcDate,
    sfcNumber = sfcNumber

)
fun PaymentAccountStatus.toDataModel() = PaymentAccountStatusData.valueOf(this.name)

fun PaymentAccount.toDataModel() = PaymentAccountData(
    id = id,
    fund = fund.toDataModel(),
    accountingYear = accountingYear.toDataModel(),
    status = status.toDataModel(),
    nationalReference = nationalReference,
    technicalAssistance = technicalAssistance,
    submissionToSfcDate = submissionToSfcDate,
    sfcNumber = sfcNumber,
    comment = comment
)

fun PaymentAccountAmountSummary.toDataModel() = PaymentAccountAmountSummaryData(
    amountsGroupedByPriority = amountsGroupedByPriority.map { it.toDataModel() },
    totals = totals.toDataModel()
)

fun PaymentAccountAmountSummaryLine.toDataModel() = PaymentAccountAmountSummaryLineData (
    priorityAxis = priorityAxis,
    totalEligibleExpenditure = totalEligibleExpenditure,
    totalPublicContribution = totalPublicContribution
)


fun List<AmountWithdrawnPerPriority>.toAmountWithDrawnDataModelList() = map { it.toDataModel() }

fun AmountWithdrawnPerPriority.toDataModel() = AmountWithdrawnPerPriorityData(
    priorityAxis = priorityAxis,
    perYear = perYear.map { it.toDataModel() },
    withdrawalTotal = withdrawalTotal,
    withdrawalPublic = withdrawalPublic

)

fun AmountWithdrawnPerYear.toDataModel() = AmountWithdrawnPerYearData(
    year = year.toDataModel(),
    withdrawalTotal = withdrawalTotal,
    withdrawalPublic = withdrawalPublic,
    withdrawalTotalOfWhichAa = withdrawalTotalOfWhichAa,
    withdrawalPublicOfWhichAa = withdrawalPublicOfWhichAa,
    withdrawalTotalOfWhichEc = withdrawalTotalOfWhichEc,
    withdrawalPublicOfWhichEc = withdrawalPublicOfWhichEc

)

fun List<ReconciledAmountPerPriority>.toReconciledAmountDataModelList() = this.map { it.toDataModel() }
fun ReconciledAmountPerPriority.toDataModel() = ReconciledAmountPerPriorityData(
    priorityAxis = priorityAxis,
    reconciledAmountTotal = reconciledAmountTotal.toDataModel(),
    reconciledAmountOfAa = reconciledAmountOfAa.toDataModel(),
    reconciledAmountOfEc = reconciledAmountOfEc.toDataModel()
)
fun ReconciledAmountByType.toDataModel() =  ReconciledAmountByTypeData(
    scenario4Sum = scenario4Sum,
    scenario3Sum = scenario3Sum,
    clericalMistakesSum = clericalMistakesSum,
    comment = comment

)
