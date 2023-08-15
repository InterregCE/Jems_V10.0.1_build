package io.cloudflight.jems.server.payments.repository

import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.payments.entity.AdvancePaymentEntity
import io.cloudflight.jems.server.payments.entity.PaymentEntity
import io.cloudflight.jems.server.payments.entity.PaymentPartnerEntity
import io.cloudflight.jems.server.payments.entity.AdvancePaymentSettlementEntity
import io.cloudflight.jems.server.payments.entity.PaymentPartnerInstallmentEntity
import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSettlement
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
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.service.toOutputUser
import org.springframework.data.domain.Page
import java.math.BigDecimal

fun List<PaymentEntity>.toListModel(
    getProject: (Long, String?) -> ProjectFull,
    getConfirm: (Long) -> PaymentConfirmedInfo
) = map {
    val lumpSum = it.projectLumpSum
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
    paymentClaimSubmissionDate = project.contractedDecision?.updated,
    lumpSumId = lumpSum.programmeLumpSum.id,
    orderNr = lumpSum.id.orderNr,
    paymentApprovalDate = lumpSum.paymentEnabledDate,
    totalEligibleAmount = lumpSum.programmeLumpSum.cost,
    fundId = fund.id,
    fundName = fund.type.name,
    amountApprovedPerFund = amountApprovedPerFund!!,
    amountPaidPerFund = paymentConfirmedInfo.amountPaidPerFund,
    dateOfLastPayment = paymentConfirmedInfo.dateOfLastPayment,
    lastApprovedVersionBeforeReadyForPayment = lumpSum.lastApprovedVersionBeforeReadyForPayment
)

fun List<PaymentRow>.toListModel() = map { it.toDetailModel() }
fun PaymentRow.toDetailModel() = PaymentPerPartner(
    projectId, partnerId, orderNr, programmeLumpSumId, programmeFundId, amountApprovedPerPartner
)

fun PaymentToCreate.toEntity(
    projectEntity: ProjectEntity,
    paymentType: PaymentType,
    lumpSum: ProjectLumpSumEntity,
    fundEntity: ProgrammeFundEntity
) = PaymentEntity(
    type = paymentType,
    project = projectEntity,
    projectCustomIdentifier = projectCustomIdentifier,
    projectAcronym = projectAcronym,
    projectLumpSum = lumpSum,
    fund = fundEntity,
    amountApprovedPerFund = amountApprovedPerFund,
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
    orderNr = payment.projectLumpSum.id.orderNr,
    programmeLumpSumId = payment.projectLumpSum.programmeLumpSum.id,
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
    lumpSumId = paymentPartner.payment.projectLumpSum.programmeLumpSum.id,
    orderNr = paymentPartner.payment.projectLumpSum.id.orderNr,
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
).also { entity ->
    entity.paymentSettlements = paymentSettlements.map { it.toEntity(entity) }.toMutableSet()
}

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
    paymentConfirmedDate = paymentConfirmedDate,
    paymentSettlements = paymentSettlements?.map { it.toModel() }?.sortedBy { it.number } ?: emptyList()
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
        partnerType = ProjectPartnerRole.valueOf(partnerRole.name),
        partnerNumber = partnerSortNumber,
        partnerAbbreviation = partnerAbbreviation ?: "",
        paymentAuthorized = isPaymentAuthorizedInfo,
        amountPaid = amountPaid,
        paymentDate = paymentDate,
        amountSettled = BigDecimal.ZERO,
        programmeFund = programmeFund?.toModel(),
        partnerContribution = idNamePairOrNull(partnerContributionId, partnerContributionName),
        partnerContributionSpf = idNamePairOrNull(partnerContributionSpfId, partnerContributionSpfName),
        paymentSettlements = paymentSettlements?.map { it.toModel() } ?: emptyList()

    )
}

fun AdvancePaymentSettlementEntity.toModel() = AdvancePaymentSettlement(
    id = id,
    number = number,
    amountSettled = amountSettled,
    settlementDate = settlementDate,
    comment = comment
)

fun AdvancePaymentSettlement.toEntity(advancePayment:AdvancePaymentEntity) = AdvancePaymentSettlementEntity(
    id = id,
    number = number,
    advancePayment = advancePayment,
    amountSettled = amountSettled,
    settlementDate = settlementDate,
    comment = comment
)
