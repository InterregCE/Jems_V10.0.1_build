package io.cloudflight.jems.server.plugin.services.payments

import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.plugin.contract.models.common.UserSummaryData
import io.cloudflight.jems.plugin.contract.models.payments.export.RegularPaymentInstallmentData
import io.cloudflight.jems.plugin.contract.models.payments.regular.PartnerPaymentData
import io.cloudflight.jems.plugin.contract.models.payments.regular.PaymentDetailData
import io.cloudflight.jems.plugin.contract.models.payments.regular.PaymentPartnerInstallmentData
import io.cloudflight.jems.plugin.contract.models.payments.regular.PaymentToProjectData
import io.cloudflight.jems.plugin.contract.models.payments.regular.PaymentTypeData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerRoleData
import io.cloudflight.jems.server.payments.model.regular.PartnerPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import java.math.BigDecimal
import java.time.ZoneId

fun PaymentDetail.toDataModel() = PaymentDetailData(
    id = id,
    paymentType = paymentType.toDataModel(),
    fundName = fund.type.name,
    projectId = projectId,
    projectCustomIdentifier = projectCustomIdentifier,
    projectAcronym = projectAcronym,
    amountApprovedPerFund = amountApprovedPerFund,
    dateOfLastPayment = dateOfLastPayment?.atStartOfDay(ZoneId.systemDefault()),
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
    fundId = fund.id,
    fundName = fund.type.name,
    amountApprovedPerFund = fundAmount,
    amountPaidPerFund = amountPaidPerFund,
    dateOfLastPayment = dateOfLastPayment?.atStartOfDay(ZoneId.systemDefault()),
    lastApprovedVersionBeforeReadyForPayment = lastApprovedVersionBeforeReadyForPayment,
    amountAuthorizedPerFund = amountAuthorizedPerFund
)
fun List<PaymentToProject>.toDataModelList() = map { it.toDataModel() }

fun PaymentType.toDataModel() = PaymentTypeData.valueOf(this.name)

fun PartnerPayment.toDataModel() = PartnerPaymentData(
    id = id,
    projectId = projectId,
    orderNr = orderNr,
    programmeLumpSumId = programmeLumpSumId,
    programmeFundId = programmeFundId,
    partnerReportId = partnerReportId,
    partnerReportNumber = partnerReportNumber,
    partnerId = partnerId,
    partnerRole = ProjectPartnerRoleData.valueOf(this.partnerRole.name),
    partnerNumber = partnerNumber,
    partnerAbbreviation = partnerAbbreviation,
    amountApprovedPerPartner = amountApprovedPerPartner,
    installments = installments.map { it.toDataModel() },
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

fun List<PartnerPayment>.toExportModel(projectId: Long) = map { it.installments.withIndex().toListExportModel(it, getRemaining(it), projectId) }

fun Iterable<IndexedValue<PaymentPartnerInstallment>>.toListExportModel(partner: PartnerPayment, remaining: BigDecimal?, projectId: Long) = map {
    (index, value) -> value.toExportModel(partner, remaining, projectId, index)
}

fun PaymentPartnerInstallment.toExportModel(partner: PartnerPayment, remaining: BigDecimal?, projectId: Long, index: Int) = RegularPaymentInstallmentData(
    partnerNumber = partner.partnerNumber,
    partnerRole = ProjectPartnerRoleData.valueOf(partner.partnerRole.name),
    partnerAbbreviatedName = partner.partnerAbbreviation,
    nameInOriginalLanguage = partner.nameInOriginalLanguage,
    nameInEnglish = partner.nameInEnglish,
    partnerCountry = partner.partnerCountry,
    partnerReportNumber = partner.partnerReportNumber,
    amountApproved = partner.amountApprovedPerPartner,
    remainingToBeAuthorized = remaining,
    installmentNumber = index + 1,
    amountPaid = amountPaid,
    comment = comment,
    authorizationDate = savePaymentDate,
    authorizedBy = savePaymentInfoUser?.email,
    paymentDate = paymentDate,
    confirmationDate = paymentConfirmedDate,
    confirmedBy = paymentConfirmedUser?.email,
    correctionId = if (correction != null) "${projectId}_AC${correction.auditControlNr}.${correction.orderNr}" else ""
)

fun getRemaining(paymentPartner: PartnerPayment): BigDecimal? {
    val installmentsSum = paymentPartner.installments.filter { it.isSavePaymentInfo!! }.sumOf { it.amountPaid!! }
    return paymentPartner.amountApprovedPerPartner?.minus(installmentsSum)
}
