package io.cloudflight.jems.server.payments.repository

import io.cloudflight.jems.server.common.entity.toInstant
import io.cloudflight.jems.server.payments.entity.PaymentEntity
import io.cloudflight.jems.server.payments.entity.PaymentPartnerEntity
import io.cloudflight.jems.server.payments.entity.PaymentPartnerInstallmentEntity
import io.cloudflight.jems.server.payments.service.model.PartnerPayment
import io.cloudflight.jems.server.payments.service.model.PaymentConfirmedInfo
import io.cloudflight.jems.server.payments.service.model.PaymentDetail
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerInstallmentUpdate
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerToCreate
import io.cloudflight.jems.server.payments.service.model.PaymentPerPartner
import io.cloudflight.jems.server.payments.service.model.PaymentRow
import io.cloudflight.jems.server.payments.service.model.PaymentToCreate
import io.cloudflight.jems.server.payments.service.model.PaymentToProject
import io.cloudflight.jems.server.payments.service.model.PaymentType
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.service.toOutputUser
import org.springframework.data.domain.Page
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime

fun Page<PaymentEntity>.toListModel(
    getLumpSum: (Long, Int) -> ProjectLumpSumEntity,
    getProject: (Long, String?) -> ProjectFull,
    getConfirm: (Long) -> PaymentConfirmedInfo
) = map {
    val lumpSum = getLumpSum.invoke(it.project.id, it.orderNr)
    it.toModel(
        lumpSum,
        getProject.invoke(it.project.id, lumpSum.lastApprovedVersionBeforeReadyForPayment),
        getConfirm.invoke(it.id)
    )
}

fun PaymentEntity.toModel(
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
    paymentApprovalDate = lumpSum.paymentEnabledDate,
    totalEligibleAmount = lumpSum.programmeLumpSum.cost,
    fundName = fund.type.name,
    amountApprovedPerFund = amountApprovedPerFund!!,
    amountPaidPerFund = paymentConfirmedInfo.amountPaidPerFund,
    dateOfLastPayment = if (paymentConfirmedInfo.dateOfLastPayment != null) {
        ZonedDateTime.ofInstant(paymentConfirmedInfo.dateOfLastPayment.toInstant(), UTC)
    } else { null } ,
    lastApprovedVersionBeforeReadyForPayment = lumpSum.lastApprovedVersionBeforeReadyForPayment
)

fun List<PaymentRow>.toListModel() = map { it.toModel() }
fun PaymentRow.toModel() = PaymentPerPartner(
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

fun List<PaymentPerPartner>.toEntityList(payment: PaymentEntity) = map { it.toEntity(payment) }
fun PaymentPerPartner.toEntity(
    payment: PaymentEntity
) = PaymentPartnerEntity(
    payment = payment,
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

fun PaymentPartnerEntity.toModel(
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

fun List<PaymentPartnerInstallmentEntity>.toModelList() = map { it.toModel() }
fun PaymentPartnerInstallmentEntity.toModel() = PaymentPartnerInstallment(
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
