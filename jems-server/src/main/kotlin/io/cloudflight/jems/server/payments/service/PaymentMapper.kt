package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.api.payments.PaymentToProjectDTO
import io.cloudflight.jems.api.payments.PaymentTypeDTO
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
