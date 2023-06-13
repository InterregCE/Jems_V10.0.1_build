package io.cloudflight.jems.server.plugin.services.payments
import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.plugin.contract.models.common.UserSummaryData
import io.cloudflight.jems.plugin.contract.models.payments.regular.PaymentDetailData
import io.cloudflight.jems.plugin.contract.models.payments.regular.PaymentToProjectData
import io.cloudflight.jems.plugin.contract.models.payments.regular.PaymentTypeData
import io.cloudflight.jems.plugin.contract.models.payments.regular.PartnerPaymentData
import io.cloudflight.jems.plugin.contract.models.payments.regular.PaymentPartnerInstallmentData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerRoleData
import io.cloudflight.jems.server.payments.model.regular.PaymentDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.model.regular.PartnerPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallment


fun PaymentDetail.toDataModel() = PaymentDetailData(
    id = id,
    paymentType = paymentType.toDataModel(),
    fundName = fundName,
    projectId = projectId,
    projectCustomIdentifier = projectCustomIdentifier,
    projectAcronym = projectAcronym,
    amountApprovedPerFund = amountApprovedPerFund,
    dateOfLastPayment = dateOfLastPayment,
    partnerPayments = partnerPayments.map { it.toDataModel() }
)

fun PaymentToProject.toDataModel() = PaymentToProjectData(
    id = id,
    paymentType = paymentType.toDataModel(),
    projectCustomIdentifier = projectCustomIdentifier,
    projectAcronym = projectAcronym,
    paymentClaimNo = paymentClaimNo,
    paymentClaimSubmissionDate = paymentClaimSubmissionDate,
    lumpSumId = lumpSumId,
    orderNr = orderNr,
    paymentApprovalDate = paymentApprovalDate,
    totalEligibleAmount = totalEligibleAmount,
    fundId = fundId,
    fundName = fundName,
    amountApprovedPerFund = amountApprovedPerFund,
    amountPaidPerFund = amountPaidPerFund,
    dateOfLastPayment = dateOfLastPayment,
    lastApprovedVersionBeforeReadyForPayment = lastApprovedVersionBeforeReadyForPayment
)
fun List<PaymentToProject>.toDataModelList() = map { it.toDataModel() }

fun PaymentType.toDataModel() = PaymentTypeData.valueOf(this.name)

fun PartnerPayment.toDataModel() = PartnerPaymentData(
    id = id,
    projectId = projectId,
    orderNr = orderNr,
    programmeLumpSumId = programmeLumpSumId,
    programmeFundId = programmeFundId,
    partnerId = partnerId,
    partnerRole = ProjectPartnerRoleData.valueOf(this.partnerRole.name),
    partnerNumber = partnerNumber,
    partnerAbbreviation = partnerAbbreviation,
    amountApprovedPerPartner = amountApprovedPerPartner,
    installments = installments.map { it.toDataModel() }
)

fun PaymentPartnerInstallment.toDataModel() = PaymentPartnerInstallmentData(
    id = id,
    fundId = fundId,
    lumpSumId = lumpSumId,
    orderNr = orderNr,
    amountPaid = amountPaid,
    paymentDate = paymentDate,
    comment = comment,
    isSavePaymentInfo = isSavePaymentInfo,
    savePaymentInfoUser = savePaymentInfoUser?.toUserSummaryDataModel(),
    savePaymentDate = savePaymentDate,
    isPaymentConfirmed = isPaymentConfirmed,
    paymentConfirmedUser = paymentConfirmedUser?.toUserSummaryDataModel(),
    paymentConfirmedDate = paymentConfirmedDate
)

fun OutputUser.toUserSummaryDataModel() = UserSummaryData(
    id = id ?: 0L,
    email = email,
    name = name,
    surname = surname
)