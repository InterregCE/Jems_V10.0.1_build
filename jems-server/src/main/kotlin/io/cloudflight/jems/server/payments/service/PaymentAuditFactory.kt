package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSettlement
import io.cloudflight.jems.server.payments.model.ec.CorrectionInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.regular.PartnerPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallmentUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.project.service.model.ProjectFull
import java.math.RoundingMode

fun monitoringFtlsReadyForPayment(
    context: Any,
    project: ProjectFull,
    ftlsId: Int,
    state: Boolean
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.FTLS_READY_FOR_PAYMENT_CHANGE)
            .project(project)
            .description(
                "Fast track lump sum $ftlsId for project ${project.customIdentifier}" +
                    " set as ${getAnswer(state)} for Ready for payment"
            )
            .build()
    )

fun paymentInstallmentConfirmed(
    context: Any,
    payment: PaymentDetail,
    partner: PartnerPayment,
    installment: PaymentPartnerInstallmentUpdate,
    installmentNr: Int,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PAYMENT_INSTALLMENT_CONFIRMED)
            .project(partner.projectId, payment.projectCustomIdentifier, payment.projectAcronym)
            .description(
                "Amount ${installment.amountPaid} EUR was confirmed for payment ${payment.id}, installment $installmentNr" +
                    " of partner ${getPartnerName(partner.partnerRole, partner.partnerNumber)}" +
                        if(payment.paymentType == PaymentType.REGULAR) " for partner report R.${partner.partnerReportNumber} " else ""
            )
            .build()
    )

fun paymentInstallmentAuthorized(
    context: Any,
    payment: PaymentDetail,
    partner: PartnerPayment,
    installment: PaymentPartnerInstallmentUpdate,
    installmentNr: Int,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PAYMENT_INSTALLMENT_AUTHORISED)
            .project(partner.projectId, payment.projectCustomIdentifier, payment.projectAcronym)
            .description(
                "Amount ${installment.amountPaid} EUR was authorised for payment ${payment.id}, installment $installmentNr" +
                        " of partner ${getPartnerName(partner.partnerRole, partner.partnerNumber)}" +
                        if(payment.paymentType == PaymentType.REGULAR) " for partner report R.${partner.partnerReportNumber} " else ""
            )
            .build()
    )

fun paymentInstallmentDeleted(
    context: Any,
    payment: PaymentDetail,
    partner: PartnerPayment,
    installmentNr: Int,
): AuditCandidateEvent =
    AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PAYMENT_INSTALLMENT_IS_DELETED)
            .project(partner.projectId, payment.projectCustomIdentifier, payment.projectAcronym)
            .description(
                "Payment installment $installmentNr for payment ${payment.id}" +
                    " of partner ${getPartnerName(partner.partnerRole, partner.partnerNumber)}" +
                        (if(payment.paymentType == PaymentType.REGULAR) " for partner report R.${partner.partnerReportNumber} " else "") + " is deleted"
            )
            .build()
    )


fun advancePaymentCreated(
    context: Any,
    paymentDetail: AdvancePaymentDetail
): AuditCandidateEvent = AuditCandidateEvent(
    context = context,
    auditCandidate = AuditBuilder(AuditAction.ADVANCE_PAYMENT_IS_CREATED)
        .project(paymentDetail.projectId, paymentDetail.projectCustomIdentifier, paymentDetail.projectAcronym)
        .description(
            "Advance payment number ${paymentDetail.id} is created for " +
                "partner ${
                    getPartnerName(
                        paymentDetail.partnerType,
                        paymentDetail.partnerNumber
                    )
                } for funding source " +
                getFundingSourceName(paymentDetail)
        )
        .build()
)

fun advancePaymentDeleted(
    context: Any,
    paymentDetail: AdvancePaymentDetail
): AuditCandidateEvent = AuditCandidateEvent(
    context = context,
    auditCandidate = AuditBuilder(AuditAction.ADVANCE_PAYMENT_IS_DELETED)
        .project(paymentDetail.projectId, paymentDetail.projectCustomIdentifier, paymentDetail.projectAcronym)
        .description(
            "Advance payment number ${paymentDetail.id} is deleted for " +
                "partner ${
                    getPartnerName(
                        paymentDetail.partnerType,
                        paymentDetail.partnerNumber
                    )
                } for funding source " +
                getFundingSourceName(paymentDetail)
        )
        .build()
)

fun advancePaymentAuthorized(
    context: Any,
    paymentDetail: AdvancePaymentDetail
): AuditCandidateEvent = AuditCandidateEvent(
    context = context,
    auditCandidate = AuditBuilder(AuditAction.ADVANCE_PAYMENT_DETAIL_AUTHORISED)
        .project(paymentDetail.projectId, paymentDetail.projectCustomIdentifier, paymentDetail.projectAcronym)
        .description(
            "Advance payment details for advance payment ${paymentDetail.id} of " +
                "partner ${getPartnerName(paymentDetail.partnerType, paymentDetail.partnerNumber)} " +
                "for funding source ${getFundingSourceName(paymentDetail)} are authorised"
        )
        .build()
)

fun advancePaymentConfirmed(
    context: Any,
    paymentDetail: AdvancePaymentDetail
): AuditCandidateEvent = AuditCandidateEvent(
    context = context,
    auditCandidate = AuditBuilder(AuditAction.ADVANCE_PAYMENT_DETAIL_CONFIRMED)
        .project(paymentDetail.projectId, paymentDetail.projectCustomIdentifier, paymentDetail.projectAcronym)
        .description(
            "Advance payment details for advance payment ${paymentDetail.id} of " +
                "partner ${getPartnerName(paymentDetail.partnerType, paymentDetail.partnerNumber)} " +
                "for funding source ${getFundingSourceName(paymentDetail)} are confirmed"
        )
        .build()
)

fun advancePaymentSettlementCreated(
    context: Any,
    settlement: AdvancePaymentSettlement,
    paymentDetail: AdvancePaymentDetail
): AuditCandidateEvent = AuditCandidateEvent(
    context = context,
    auditCandidate = AuditBuilder(AuditAction.ADVANCE_PAYMENT_SETTLEMENT_CREATED)
        .project(paymentDetail.projectId, paymentDetail.projectCustomIdentifier, paymentDetail.projectAcronym)
        .description(
            "${
                settlement.amountSettled.setScale(
                    2,
                    RoundingMode.HALF_UP
                )
            } EUR was settled in settlement no. ${settlement.number} " +
                "for advance payment no. ${paymentDetail.id} of partner ${
                    getPartnerName(
                        paymentDetail.partnerType,
                        paymentDetail.partnerNumber
                    )
                } " +
                "for funding source ${paymentDetail.programmeFund?.abbreviation?.extractTranslation(SystemLanguage.EN)} is created"
        )
        .build()
)

fun advancePaymentSettlementDeleted(
    context: Any,
    settlement: AdvancePaymentSettlement,
    paymentDetail: AdvancePaymentDetail
): AuditCandidateEvent = AuditCandidateEvent(
    context = context,
    auditCandidate = AuditBuilder(AuditAction.ADVANCE_PAYMENT_SETTLEMENT_DELETED)
        .project(paymentDetail.projectId, paymentDetail.projectCustomIdentifier, paymentDetail.projectAcronym)
        .description(
            "${settlement.amountSettled} EUR was settled in settlement no. ${settlement.number} " +
                "for advance payment no. ${paymentDetail.id} of partner ${
                    getPartnerName(
                        paymentDetail.partnerType,
                        paymentDetail.partnerNumber
                    )
                } " +
                "for funding source ${paymentDetail.programmeFund?.abbreviation?.extractTranslation(SystemLanguage.EN)} is deleted"
        )
        .build()
)

fun paymentApplicationToEcCreated(
    context: Any,
    paymentApplicationToEc: PaymentApplicationToEcDetail
): AuditCandidateEvent = AuditCandidateEvent(
    context = context,
    auditCandidate = AuditBuilder(AuditAction.PAYMENT_APPLICATION_TO_EC_IS_CREATED)
        .description(
            "Payment application to EC number ${paymentApplicationToEc.id} " +
                "was created for Fund (${paymentApplicationToEc.paymentApplicationToEcSummary.programmeFund.id}, " +
                "${paymentApplicationToEc.paymentApplicationToEcSummary.programmeFund.type}) " +
                "for accounting Year ${computeYearNumber(paymentApplicationToEc.paymentApplicationToEcSummary.accountingYear.startDate)}: ${
                    paymentApplicationToEc.paymentApplicationToEcSummary.accountingYear.startDate
                } - ${paymentApplicationToEc.paymentApplicationToEcSummary.accountingYear.endDate}"
        )
        .build()
)

fun paymentApplicationToEcFinished(
    context: Any,
    updatedEcPaymentApplication: PaymentApplicationToEcDetail,
    includedPayments: Map<Long, PaymentInEcPaymentMetadata> = emptyMap(),
    includedCorrections: Map<Long, CorrectionInEcPaymentMetadata> = emptyMap(),
): AuditCandidateEvent {
    val ftlsPayments = includedPayments.filter { it.value.type == PaymentType.FTLS }
    val regularPayments = includedPayments.filter { it.value.type == PaymentType.REGULAR }
    return AuditCandidateEvent(
        context = context,
        auditCandidate = AuditBuilder(AuditAction.PAYMENT_APPLICATION_TO_EC_STATUS_CHANGED)
            .description(
                updatedEcPaymentApplication
                    .toDescription(previousStatus = PaymentEcStatus.Draft, newStatus = PaymentEcStatus.Finished) + " " +
                        "and the following items were included:\n" +
                        "FTLS [${ftlsPayments.keys.joinToString(", ")}]\n" +
                        "Regular [${regularPayments.keys.joinToString(", ")}]\n" +
                        "Correction [${includedCorrections.formCorrectionId().joinToString(", ")}]"
            )
            .build()
    )
}

fun paymentApplicationToEcReOpened(
    context: Any,
    updatedEcPaymentApplication: PaymentApplicationToEcDetail,
) = AuditCandidateEvent(
    context = context,
    auditCandidate = AuditBuilder(AuditAction.PAYMENT_APPLICATION_TO_EC_STATUS_CHANGED)
        .description(
            updatedEcPaymentApplication
                .toDescription(previousStatus = PaymentEcStatus.Finished, newStatus = PaymentEcStatus.Draft)
        )
        .build()
    )

fun paymentApplicationToEcDeleted(
    context: Any,
    paymentApplicationToEc: PaymentApplicationToEcDetail,
): AuditCandidateEvent = AuditCandidateEvent(
    context = context,
    auditCandidate = AuditBuilder(AuditAction.PAYMENT_APPLICATION_TO_EC_IS_DELETED)
        .description(
            "Payment application to EC number ${paymentApplicationToEc.id} " +
                "created for Fund (${paymentApplicationToEc.paymentApplicationToEcSummary.programmeFund.id}, " +
                "${paymentApplicationToEc.paymentApplicationToEcSummary.programmeFund.type}) " +
                "for accounting Year ${computeYearNumber(paymentApplicationToEc.paymentApplicationToEcSummary.accountingYear.startDate)}: ${
                        paymentApplicationToEc.paymentApplicationToEcSummary.accountingYear.startDate
                } - ${paymentApplicationToEc.paymentApplicationToEcSummary.accountingYear.endDate} was deleted"
        )
        .build()
)

fun paymentApplicationToEcAuditExportCreated(
    context: Any,
    programmeFundType: String?,
    accountingYear: Short?
): AuditCandidateEvent = AuditCandidateEvent(
    context = context,
    auditCandidate = AuditBuilder(AuditAction.PAYMENT_APPLICATION_TO_EC_AUDIT_EXPORT_TRIGGERED)
        .description(
            "An audit export was generated for payments to ec with programme fund type ${programmeFundType} and accounting year " +
                    "${accountingYear}"
        )
        .build()
)

private fun getAnswer(state: Boolean): String {
    return if (state) {
        "YES"
    } else {
        "NO"
    }
}

private fun getFundingSourceName(paymentDetail: AdvancePaymentDetail): String {
    return when {
        paymentDetail.programmeFund != null ->
            "(${paymentDetail.programmeFund.id}, ${paymentDetail.programmeFund.type})"

        paymentDetail.partnerContribution != null -> paymentDetail.partnerContribution.name
        paymentDetail.partnerContributionSpf != null -> paymentDetail.partnerContributionSpf.name
        else -> ""
    }
}
