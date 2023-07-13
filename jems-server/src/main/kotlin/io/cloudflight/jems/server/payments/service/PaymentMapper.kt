package io.cloudflight.jems.server.payments.service

import io.cloudflight.jems.api.common.dto.IdNamePairDTO
import io.cloudflight.jems.api.payments.dto.AdvancePaymentDTO
import io.cloudflight.jems.api.payments.dto.AdvancePaymentDetailDTO
import io.cloudflight.jems.api.payments.dto.AdvancePaymentSearchRequestDTO
import io.cloudflight.jems.api.payments.dto.AdvancePaymentUpdateDTO
import io.cloudflight.jems.api.payments.dto.AdvancePaymentSettlementDTO
import io.cloudflight.jems.api.payments.dto.PaymentDetailDTO
import io.cloudflight.jems.api.payments.dto.PaymentPartnerDTO
import io.cloudflight.jems.api.payments.dto.PaymentPartnerInstallmentDTO
import io.cloudflight.jems.api.payments.dto.PaymentSearchRequestDTO
import io.cloudflight.jems.api.payments.dto.PaymentToProjectDTO
import io.cloudflight.jems.api.payments.dto.PaymentTypeDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSearchRequest
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentUpdate
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSettlement
import io.cloudflight.jems.server.payments.model.regular.PartnerPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallmentUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
import io.cloudflight.jems.server.programme.controller.fund.toDto
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

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
    projectId = projectId,
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
        savePaymentInfoUserId = it.savePaymentInfoUser?.id,
        savePaymentDate = it.savePaymentDate,
        isPaymentConfirmed = it.paymentConfirmed,
        paymentConfirmedUserId = it.paymentConfirmedUser?.id,
        paymentConfirmedDate = it.paymentConfirmedDate
    )
}

// Advance Payment

fun AdvancePaymentDetail.toDTO() = AdvancePaymentDetailDTO(
    id = id,
    projectId = projectId,
    projectCustomIdentifier = projectCustomIdentifier,
    projectAcronym = projectAcronym,
    projectVersion = projectVersion,
    partnerId = partnerId,
    partnerType = ProjectPartnerRoleDTO.valueOf(partnerType.name),
    partnerNumber = partnerNumber,
    partnerAbbreviation = partnerAbbreviation,
    programmeFund = programmeFund?.toDto(),
    partnerContribution = idNamePairDtoOrNull(partnerContribution),
    partnerContributionSpf = idNamePairDtoOrNull(partnerContributionSpf),
    amountPaid = amountPaid,
    paymentDate = paymentDate,
    comment = comment,
    paymentAuthorized = paymentAuthorized,
    paymentAuthorizedUser = paymentAuthorizedUser,
    paymentAuthorizedDate = paymentAuthorizedDate,
    paymentConfirmed = paymentConfirmed,
    paymentConfirmedUser = paymentConfirmedUser,
    paymentConfirmedDate = paymentConfirmedDate,
    paymentSettlements = paymentSettlements.map { it.toDto() }

)

private fun idNamePairDtoOrNull(idName: IdNamePair?): IdNamePairDTO? {
    return if (idName != null) {
        IdNamePairDTO(idName.id, idName.name)
    } else null
}

fun AdvancePaymentUpdateDTO.toModel() = AdvancePaymentUpdate(
    id = id,
    projectId = projectId,
    partnerId = partnerId,
    programmeFundId = programmeFundId,
    partnerContributionId = partnerContributionId,
    partnerContributionSpfId = partnerContributionSpfId,
    amountPaid = amountPaid,
    paymentDate = paymentDate,
    comment = comment,
    paymentAuthorized = paymentAuthorized,
    paymentConfirmed = paymentConfirmed,
    paymentSettlements = paymentSettlements.map { it.toModel() }
)

fun AdvancePayment.toDTO() = AdvancePaymentDTO(
    id = id,
    projectCustomIdentifier = projectCustomIdentifier,
    projectAcronym = projectAcronym,
    partnerType = ProjectPartnerRoleDTO.valueOf(partnerType.name),
    partnerNumber = partnerNumber,
    partnerAbbreviation = partnerAbbreviation,
    programmeFund = programmeFund?.toDto(),
    partnerContribution = idNamePairDtoOrNull(partnerContribution),
    partnerContributionSpf = idNamePairDtoOrNull(partnerContributionSpf),
    paymentAuthorized = paymentAuthorized,
    amountPaid = amountPaid,
    paymentDate = paymentDate,
    amountSettled = amountSettled
)

fun AdvancePaymentSettlement.toDto() = AdvancePaymentSettlementDTO(
    id = id,
    number = number,
    amountSettled = amountSettled,
    settlementDate = settlementDate,
    comment = comment
)

fun AdvancePaymentSettlementDTO.toModel() = AdvancePaymentSettlement(
    id = id,
    number = number,
    amountSettled = amountSettled,
    settlementDate = settlementDate,
    comment = comment
)

private val mapper = Mappers.getMapper(PaymentMapper::class.java)

fun AdvancePaymentSearchRequestDTO.toModel(): AdvancePaymentSearchRequest = mapper.map(this)
fun PaymentSearchRequestDTO.toModel(): PaymentSearchRequest = mapper.map(this)

@Mapper
interface PaymentMapper {
    fun map(dto: AdvancePaymentSearchRequestDTO): AdvancePaymentSearchRequest
    fun map(dto: PaymentSearchRequestDTO): PaymentSearchRequest
}
