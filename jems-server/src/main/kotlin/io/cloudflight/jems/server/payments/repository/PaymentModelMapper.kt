package io.cloudflight.jems.server.payments.repository

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.common.entity.toInstant
import io.cloudflight.jems.server.payments.entity.AdvancePaymentEntity
import io.cloudflight.jems.server.payments.entity.PaymentEntity
import io.cloudflight.jems.server.payments.entity.PaymentPartnerEntity
import io.cloudflight.jems.server.payments.entity.PaymentPartnerInstallmentEntity
import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentUpdate
import io.cloudflight.jems.server.payments.model.regular.PartnerPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentConfirmedInfo
import io.cloudflight.jems.server.payments.model.regular.PaymentDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallmentUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerToCreate
import io.cloudflight.jems.server.payments.model.regular.PaymentPerPartner
import io.cloudflight.jems.server.payments.model.regular.PaymentRow
import io.cloudflight.jems.server.payments.model.regular.PaymentToCreate
import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.service.toOutputUser
import org.springframework.data.domain.Page
import java.math.BigDecimal
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime

fun Page<PaymentEntity>.toListModel(
    getLumpSum: (Long, Int) -> ProjectLumpSumEntity,
    getProject: (Long, String?) -> ProjectFull,
    getConfirm: (Long) -> PaymentConfirmedInfo
) = map {
    val lumpSum = getLumpSum.invoke(it.project.id, it.orderNr)
    it.toDetailModel(
        lumpSum,
        getProject.invoke(it.project.id, lumpSum.lastApprovedVersionBeforeReadyForPayment),
        getConfirm.invoke(it.id)
    )
}

fun List<PaymentEntity>.toListModel(
    getLumpSum: (Long, Int) -> ProjectLumpSumEntity,
    getProject: (Long, String?) -> ProjectFull,
    getConfirm: (Long) -> PaymentConfirmedInfo
) = map {
    val lumpSum = getLumpSum.invoke(it.project.id, it.orderNr)
    it.toDetailModel(
        lumpSum,
        getProject.invoke(it.project.id, lumpSum.lastApprovedVersionBeforeReadyForPayment),
        getConfirm.invoke(it.id)
    )
}

fun PaymentEntity.toDetailModel(
    lumpSum: ProjectLumpSumEntity,
    projectFull: ProjectFull,
    paymentConfirmedInfo: PaymentConfirmedInfo
) = PaymentToProject(
    id = id,
    paymentType = type,
    projectCustomIdentifier = projectFull.customIdentifier,
    projectAcronym = projectFull.acronym,
    paymentClaimNo = 0,
    paymentClaimSubmissionDate = projectFull.contractedDecision?.updated,
    lumpSumId = lumpSum.programmeLumpSum.id,
    orderNr = orderNr,
    paymentApprovalDate = lumpSum.paymentEnabledDate,
    totalEligibleAmount = lumpSum.programmeLumpSum.cost,
    fundId = fund.id,
    fundName = fund.type.name,
    amountApprovedPerFund = amountApprovedPerFund!!,
    amountPaidPerFund = paymentConfirmedInfo.amountPaidPerFund,
    dateOfLastPayment = if (paymentConfirmedInfo.dateOfLastPayment != null) {
        ZonedDateTime.ofInstant(paymentConfirmedInfo.dateOfLastPayment.toInstant(), UTC)
    } else { null } ,
    lastApprovedVersionBeforeReadyForPayment = lumpSum.lastApprovedVersionBeforeReadyForPayment
)

fun List<PaymentRow>.toListModel() = map { it.toDetailModel() }
fun PaymentRow.toDetailModel() = PaymentPerPartner(
    projectId, partnerId, orderNr, programmeLumpSumId, programmeFundId, amountApprovedPerPartner
)

fun PaymentToCreate.toEntity(
    projectEntity: ProjectEntity,
    paymentType: PaymentType,
    orderNr: Int,
    fundEntity: ProgrammeFundEntity
) = PaymentEntity(
    type = paymentType,
    project = projectEntity,
    orderNr = orderNr,
    programmeLumpSumId = programmeLumpSumId,
    fund = fundEntity,
    amountApprovedPerFund = amountApprovedPerFund
)

fun PaymentPartnerToCreate.toEntity(paymentEntity: PaymentEntity) = PaymentPartnerEntity (
    payment = paymentEntity,
    partnerId = partnerId,
    amountApprovedPerPartner = amountApprovedPerPartner
)

fun PaymentEntity.toDetailModel(
    partnerPayments: List<PartnerPayment>
) = PaymentDetail(
    id = id,
    paymentType = PaymentType.valueOf(type.name),
    projectId = project.id,
    projectCustomIdentifier = project.customIdentifier,
    projectAcronym = project.acronym,
    fundName = fund.type.name,
    amountApprovedPerFund = amountApprovedPerFund!!,
    dateOfLastPayment = null,
    partnerPayments = partnerPayments
)

// Payment Partner

fun PaymentPartnerEntity.toDetailModel(
    partnerDetail: ProjectPartnerDetail,
    installments: List<PaymentPartnerInstallment>
) = PartnerPayment(
    id = id,
    projectId = payment.project.id,
    orderNr = payment.orderNr,
    programmeLumpSumId = payment.programmeLumpSumId,
    programmeFundId = payment.fund.id,
    partnerId = partnerId,
    partnerRole = partnerDetail.role,
    partnerAbbreviation = partnerDetail.abbreviation,
    partnerNumber = partnerDetail.sortNumber,
    amountApprovedPerPartner = amountApprovedPerPartner,
    installments = installments
)

// Payment Partner Installment

fun List<PaymentPartnerInstallmentEntity>.toModelList() = map { it.toDetailModel() }
fun PaymentPartnerInstallmentEntity.toDetailModel() = PaymentPartnerInstallment(
    id = id,
    fundId = paymentPartner.payment.fund.id,
    lumpSumId = paymentPartner.payment.programmeLumpSumId,
    orderNr = paymentPartner.payment.orderNr,
    amountPaid = amountPaid,
    paymentDate = paymentDate,
    comment = comment,
    isSavePaymentInfo = isSavePaymentInfo,
    savePaymentInfoUser = savePaymentInfoUser?.toOutputUser(),
    savePaymentDate = savePaymentDate,
    isPaymentConfirmed = isPaymentConfirmed,
    paymentConfirmedUser = paymentConfirmedUser?.toOutputUser(),
    paymentConfirmedDate = paymentConfirmedDate
)

fun PaymentPartnerInstallmentUpdate.toEntity(
    paymentPartner: PaymentPartnerEntity,
    savePaymentInfoUser: UserEntity?,
    paymentConfirmedUser: UserEntity?
) = PaymentPartnerInstallmentEntity(
    id = id ?: 0,
    paymentPartner = paymentPartner,
    amountPaid = amountPaid,
    paymentDate = paymentDate,
    comment = comment,
    isSavePaymentInfo = isSavePaymentInfo,
    savePaymentInfoUser = savePaymentInfoUser,
    savePaymentDate = savePaymentDate,
    isPaymentConfirmed = isPaymentConfirmed,
    paymentConfirmedUser = paymentConfirmedUser,
    paymentConfirmedDate = paymentConfirmedDate
)

// Advance Payment

fun AdvancePaymentUpdate.toEntity(
    project: ProjectFull,
    projectVersion: String,
    partner: ProjectPartnerSummary,
    paymentAuthorizedUser: UserEntity?,
    paymentConfirmedUser: UserEntity?
) = AdvancePaymentEntity(
    id = id ?: 0,
    projectId = project.id!!,
    projectCustomIdentifier = project.customIdentifier,
    projectAcronym = project.acronym,
    projectVersion = projectVersion,
    partnerId = partner.id!!,
    partnerRole = partner.role,
    partnerSortNumber = partner.sortNumber,
    partnerAbbreviation = partner.abbreviation,
    amountPaid = amountPaid,
    paymentDate = paymentDate,
    comment = comment,
    isPaymentAuthorizedInfo = paymentAuthorized,
    paymentAuthorizedInfoUser = paymentAuthorizedUser,
    paymentAuthorizedDate = paymentAuthorizedDate,
    isPaymentConfirmed = paymentConfirmed,
    paymentConfirmedUser = paymentConfirmedUser,
    paymentConfirmedDate = paymentConfirmedDate
)

fun AdvancePaymentEntity.toDetailModel() = AdvancePaymentDetail(
    id = id,
    projectId = projectId,
    projectCustomIdentifier = projectCustomIdentifier,
    projectAcronym = projectAcronym ?: "",
    projectVersion = projectVersion,
    partnerId = partnerId,
    partnerType = partnerRole,
    partnerNumber = partnerSortNumber,
    partnerAbbreviation = partnerAbbreviation ?: "",
    programmeFund = programmeFund?.toModel(),
    partnerContribution = idNamePairOrNull(partnerContributionId, partnerContributionName),
    partnerContributionSpf = idNamePairOrNull(partnerContributionSpfId, partnerContributionSpfName),
    amountPaid = amountPaid,
    paymentDate = paymentDate,
    comment = comment,
    paymentAuthorized = isPaymentAuthorizedInfo,
    paymentAuthorizedUser = paymentAuthorizedInfoUser?.toOutputUser(),
    paymentAuthorizedDate = paymentAuthorizedDate,
    paymentConfirmed = isPaymentConfirmed,
    paymentConfirmedUser = paymentConfirmedUser?.toOutputUser(),
    paymentConfirmedDate = paymentConfirmedDate
)

private fun idNamePairOrNull(id: Long?, name: String?): IdNamePair? {
    return if (id != null && name != null) {
        IdNamePair(id, name)
    } else null
}

fun Page<AdvancePaymentEntity>.toModelList() = map { it.toModel() }

fun AdvancePaymentEntity.toModel(): AdvancePayment {
    return AdvancePayment(
        id = id,
        projectCustomIdentifier = projectCustomIdentifier,
        projectAcronym = projectAcronym ?: "",
        partnerType = ProjectPartnerRoleDTO.valueOf(partnerRole.name),
        partnerNumber = partnerSortNumber,
        partnerAbbreviation = partnerAbbreviation ?: "",
        paymentAuthorized = isPaymentAuthorizedInfo,
        amountPaid = amountPaid,
        paymentDate = paymentDate,
        amountSettled = BigDecimal.ZERO,
        programmeFund = programmeFund?.toModel(),
        partnerContribution = idNamePairOrNull(partnerContributionId, partnerContributionName),
        partnerContributionSpf = idNamePairOrNull(partnerContributionSpfId, partnerContributionSpfName)
    )
}
