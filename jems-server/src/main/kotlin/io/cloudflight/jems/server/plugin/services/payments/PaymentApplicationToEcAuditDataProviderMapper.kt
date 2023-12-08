package io.cloudflight.jems.server.plugin.services.payments

import io.cloudflight.jems.plugin.contract.models.payments.export.AccountingYearData
import io.cloudflight.jems.plugin.contract.models.payments.export.AuditControlCorrectionData
import io.cloudflight.jems.plugin.contract.models.payments.export.ControllingBodyData
import io.cloudflight.jems.plugin.contract.models.payments.export.PaymentApplicationToEcData
import io.cloudflight.jems.plugin.contract.models.payments.export.PaymentApplicationToEcSummaryData
import io.cloudflight.jems.plugin.contract.models.payments.export.PaymentToEcAmountSummaryData
import io.cloudflight.jems.plugin.contract.models.payments.export.PaymentToEcAmountSummaryLineData
import io.cloudflight.jems.plugin.contract.models.payments.export.PaymentToEcCorrectionLinkingData
import io.cloudflight.jems.plugin.contract.models.payments.export.PaymentToEcPaymentData
import io.cloudflight.jems.plugin.contract.models.payments.export.ProjectCorrectionProgrammeMeasureScenarioData
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinking
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import io.cloudflight.jems.server.payments.model.ec.export.PaymentApplicationToEcFull
import io.cloudflight.jems.server.plugin.services.toDataModel
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection

fun PaymentApplicationToEcFull.toDataModel() = PaymentApplicationToEcData(
    id = id,
    paymentApplicationToEcSummary = paymentApplicationToEcSummary.toDataModel(),
    paymentToEcAmountSummary = paymentToEcAmountSummary.toDataModel(),
    corrections = corrections.map {it.toDataModel()},
    regularProjectPayments = regularProjectPayments.map {it.toDataModel()}
)

fun List<PaymentApplicationToEcFull>.toDataModelList() = map { it.toDataModel() }

fun PaymentApplicationToEcSummary.toDataModel() = PaymentApplicationToEcSummaryData(
    programmeFund = programmeFund.toDataModel(),
    accountingYear = accountingYear.toDataModel(),
    nationalReference = nationalReference,
    technicalAssistanceEur = technicalAssistanceEur,
    submissionToSfcDate = submissionToSfcDate,
    sfcNumber = sfcNumber,
    comment = comment
)

fun AccountingYear.toDataModel() = AccountingYearData(
    id = id,
    year = year,
    startDate = startDate,
    endDate = endDate
)

fun PaymentToEcAmountSummary.toDataModel() = PaymentToEcAmountSummaryData(
    amountsGroupedByPriority = amountsGroupedByPriority.map { it.toDataModel() },
    totals = totals.toDataModel()
)

fun PaymentToEcAmountSummaryLine.toDataModel() = PaymentToEcAmountSummaryLineData(
    priorityAxis = priorityAxis,
    totalEligibleExpenditure = totalEligibleExpenditure,
    totalUnionContribution = totalUnionContribution,
    totalPublicContribution = totalPublicContribution
)

fun PaymentToEcCorrectionLinking.toDataModel() = PaymentToEcCorrectionLinkingData(
    correction = correction.toDataModel(),
    projectId = projectId,
    projectAcronym = projectAcronym,
    projectCustomIdentifier = projectCustomIdentifier,
    priorityAxis = priorityAxis,
    controllingBody = ControllingBodyData.valueOf(controllingBody.name),
    scenario = ProjectCorrectionProgrammeMeasureScenarioData.valueOf(scenario.name),
    projectFlagged94Or95 = projectFlagged94Or95,
    paymentToEcId = paymentToEcId,
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

fun AuditControlCorrection.toDataModel() = AuditControlCorrectionData(
    id = id,
    orderNr = orderNr,
    auditControlId = auditControlId,
    auditControlNr = auditControlNr
)

fun PaymentToEcPayment.toDataModel() = PaymentToEcPaymentData(
    payment = payment.toDataModel(),
    paymentToEcId = paymentToEcId,
    partnerContribution = partnerContribution,
    publicContribution = publicContribution,
    correctedPublicContribution = correctedPublicContribution,
    autoPublicContribution = autoPublicContribution,
    correctedAutoPublicContribution = correctedAutoPublicContribution,
    privateContribution = privateContribution,
    correctedPrivateContribution = correctedPrivateContribution,
    priorityAxis = priorityAxis
)