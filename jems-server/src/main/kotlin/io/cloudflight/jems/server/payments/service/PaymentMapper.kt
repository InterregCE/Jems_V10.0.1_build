package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.api.payments.PaymentDetailDTO
import io.cloudflight.jems.api.payments.PaymentPartnerDTO
import io.cloudflight.jems.api.payments.PaymentPartnerInstallmentDTO
import io.cloudflight.jems.api.payments.PaymentToProjectDTO
import io.cloudflight.jems.api.payments.PaymentTypeDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.payments.service.model.PartnerPayment
import io.cloudflight.jems.server.payments.service.model.PaymentDetail
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerInstallmentUpdate
import io.cloudflight.jems.server.payments.service.model.PaymentToProject

fun PaymentToProject.toDTO() = PaymentToProjectDTO(
    id = id,
    paymentType = PaymentTypeDTO.valueOf(paymentType.name),
    projectCustomIdentifier = projectCustomIdentifier,
    projectAcronym = projectAcronym,
    paymentClaimNo = paymentClaimNo,
    paymentClaimSubmissionDate = paymentClaimSubmissionDate,
    paymentApprovalDate = paymentApprovalDate,
    totalEligibleAmount = totalEligibleAmount,
    fundName = fundName,
    amountApprovedPerFund = amountApprovedPerFund,
    amountPaidPerFund = amountPaidPerFund,
    dateOfLastPayment = dateOfLastPayment,
    lastApprovedVersionBeforeReadyForPayment = lastApprovedVersionBeforeReadyForPayment
)

fun PaymentDetail.toDTO() = PaymentDetailDTO(
    id = id,
    paymentType = PaymentTypeDTO.valueOf(paymentType.name),
    fundName = fundName,
    projectCustomIdentifier = projectCustomIdentifier,
    projectAcronym = projectAcronym,
    amountApprovedPerFund = amountApprovedPerFund,
    dateOfLastPayment = dateOfLastPayment,
    partnerPayments = partnerPayments.map { it.toDTO() }
)

fun PartnerPayment.toDTO() = PaymentPartnerDTO(
    id = id,
    partnerId = partnerId,
    partnerType = ProjectPartnerRoleDTO.valueOf(partnerRole.name),
    partnerNumber = partnerNumber,
    partnerAbbreviation = partnerAbbreviation,
    amountApproved = amountApprovedPerPartner,
    installments = installments.map { it.toDTO() }
)

// Payment Partner Installment

fun PaymentPartnerInstallment.toDTO() = PaymentPartnerInstallmentDTO(
    id = id,
    amountPaid = amountPaid,
    paymentDate = paymentDate,
    comment = comment,
    savePaymentInfo = isSavePaymentInfo,
    savePaymentInfoUser = savePaymentInfoUser,
    savePaymentDate = savePaymentDate,
    paymentConfirmed = isPaymentConfirmed,
    paymentConfirmedUser = paymentConfirmedUser,
    paymentConfirmedDate = paymentConfirmedDate
)

fun Iterable<PaymentPartnerInstallmentDTO>.toModelList() = map {
    PaymentPartnerInstallmentUpdate(
        id = it.id,
        amountPaid = it.amountPaid,
        paymentDate = it.paymentDate,
        comment = it.comment,
        isSavePaymentInfo = it.savePaymentInfo,
        isPaymentConfirmed = it.paymentConfirmed
    )
}
