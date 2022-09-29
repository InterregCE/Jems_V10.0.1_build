package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.api.payments.PaymentDetailDTO
import io.cloudflight.jems.api.payments.PaymentPartnerDTO
import io.cloudflight.jems.api.payments.PaymentToProjectDTO
import io.cloudflight.jems.api.payments.PaymentTypeDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.payments.service.model.PartnerPayment
import io.cloudflight.jems.server.payments.service.model.PaymentDetail
import io.cloudflight.jems.server.payments.service.model.PaymentToProject

fun PaymentToProject.toDTO() = PaymentToProjectDTO(
    id = id,
    paymentType = PaymentTypeDTO.valueOf(paymentType.name),
    projectId = projectId,
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
    projectId = projectId,
    fundName = fundName,
    projectAcronym = projectAcronym,
    amountApprovedPerFund = amountApprovedPerFund,
    dateOfLastPayment = dateOfLastPayment,
    partnerPayments = partnerPayments.map { it.toDTO() }
)

fun PartnerPayment.toDTO() = PaymentPartnerDTO(
    id = id,
    projectId = projectId,
    partnerId = partnerId,
    partnerType = ProjectPartnerRoleDTO.valueOf(partnerRole.name),
    partnerNumber = partnerNumber,
    partnerAbbreviation = partnerAbbreviation,
    amountApproved = amountApprovedPerPartner
)
