package io.cloudflight.jems.server.payments.repository

import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.payments.entity.AdvancePaymentEntity
import io.cloudflight.jems.server.payments.entity.AdvancePaymentSettlementEntity
import io.cloudflight.jems.server.payments.entity.PaymentEntity
import io.cloudflight.jems.server.payments.entity.PaymentPartnerEntity
import io.cloudflight.jems.server.payments.entity.PaymentPartnerInstallmentEntity
import io.cloudflight.jems.server.payments.entity.PaymentToEcExtensionEntity
import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSettlement
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentUpdate
import io.cloudflight.jems.server.payments.model.regular.PartnerPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentConfirmedInfo
import io.cloudflight.jems.server.payments.model.regular.PaymentDetail
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallmentUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentPerPartner
import io.cloudflight.jems.server.payments.model.regular.PaymentRow
import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
import io.cloudflight.jems.server.payments.model.regular.PaymentToProjectTmp
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.model.regular.toCreate.PaymentFtlsToCreate
import io.cloudflight.jems.server.payments.model.regular.toCreate.PaymentPartnerToCreate
import io.cloudflight.jems.server.payments.model.regular.toCreate.PaymentRegularToCreate
import io.cloudflight.jems.server.payments.model.regular.toCreate.PaymentToCreate
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.toSimpleModel
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.service.toOutputUser
import org.springframework.data.domain.Page
import java.math.BigDecimal

fun List<PaymentEntity>.toListModel(
    getConfirm: (Long) -> PaymentConfirmedInfo
) = map {
    val lumpSum = it.projectLumpSum
    it.toDetailModel(
        lumpSum,
        getConfirm.invoke(it.id)
    )
}

fun PaymentEntity.toDetailModel(
    lumpSum: ProjectLumpSumEntity?,
    paymentConfirmedInfo: PaymentConfirmedInfo
) = PaymentToProject(
    id = id,
    paymentType = type,
    projectId = project.id,
    projectCustomIdentifier = this.projectCustomIdentifier,
    projectAcronym = this.projectAcronym,
    paymentClaimId = if (type == PaymentType.FTLS) null else projectReport!!.id,
    paymentClaimNo = if (type == PaymentType.FTLS) 0 else projectReport!!.number,
    paymentClaimSubmissionDate = project.contractedDecision?.updated,
    paymentToEcId = null,
    lumpSumId = lumpSum?.programmeLumpSum?.id,
    orderNr = lumpSum?.id?.orderNr,
    paymentApprovalDate = lumpSum?.paymentEnabledDate,
    totalEligibleAmount = lumpSum?.programmeLumpSum?.cost ?: BigDecimal.ZERO,
    fundId = fund.id,
    fundName = fund.type.name,
    amountApprovedPerFund = amountApprovedPerFund!!,
    amountPaidPerFund = paymentConfirmedInfo.amountPaidPerFund,
    amountAuthorizedPerFund = paymentConfirmedInfo.amountAuthorizedPerFund,
    dateOfLastPayment = paymentConfirmedInfo.dateOfLastPayment,
    lastApprovedVersionBeforeReadyForPayment = lumpSum?.lastApprovedVersionBeforeReadyForPayment,
)

fun PaymentToProjectTmp.toRegularPaymentModel() = PaymentToProject(
    id = payment.id,
    paymentType = payment.type,
    projectId = payment.project.id,
    projectCustomIdentifier = payment.projectCustomIdentifier,
    projectAcronym = payment.projectAcronym,
    paymentClaimId = payment.projectReport!!.id,
    paymentClaimNo = payment.projectReport!!.number,
    paymentClaimSubmissionDate = payment.projectReport?.firstSubmission,
    paymentToEcId = paymentToEcExtension.paymentToEcId,
    lumpSumId = null,
    orderNr = null,
    paymentApprovalDate = payment.projectReport?.verificationEndDate,
    totalEligibleAmount = totalEligibleForRegular!!,
    fundId = payment.fund.id,
    fundName = payment.fund.type.name,
    amountApprovedPerFund = payment.amountApprovedPerFund!!,
    amountPaidPerFund = amountPaid ?: BigDecimal.ZERO,
    amountAuthorizedPerFund = amountAuthorized ?: BigDecimal.ZERO,
    dateOfLastPayment = lastPaymentDate,
    lastApprovedVersionBeforeReadyForPayment = null,
)

fun PaymentToProjectTmp.toFTLSPaymentModel() = PaymentToProject(
    id = payment.id,
    paymentType = payment.type,
    projectId = payment.project.id,
    projectCustomIdentifier = payment.projectCustomIdentifier,
    projectAcronym = payment.projectAcronym,
    paymentClaimId = null,
    paymentClaimNo = 0,
    paymentClaimSubmissionDate = payment.project.contractedDecision?.updated,
    paymentToEcId = paymentToEcExtension.paymentToEcId,
    lumpSumId = payment.projectLumpSum?.programmeLumpSum?.id,
    orderNr = payment.projectLumpSum?.id?.orderNr,
    paymentApprovalDate = payment.projectLumpSum?.paymentEnabledDate,
    totalEligibleAmount = payment.projectLumpSum?.programmeLumpSum?.cost!!,
    fundId = payment.fund.id,
    fundName = payment.fund.type.name,
    amountApprovedPerFund = payment.amountApprovedPerFund!!,
    amountPaidPerFund = amountPaid ?: BigDecimal.ZERO,
    amountAuthorizedPerFund = amountAuthorized ?: BigDecimal.ZERO,
    dateOfLastPayment = lastPaymentDate,
    lastApprovedVersionBeforeReadyForPayment = payment.projectLumpSum?.lastApprovedVersionBeforeReadyForPayment,
)

fun List<PaymentRow>.toListModel() = map { it.toDetailModel() }
private fun PaymentRow.toDetailModel() = PaymentPerPartner(
    projectId, partnerId, orderNr, programmeLumpSumId, programmeFundId, amountApprovedPerPartner
)

fun PaymentRegularToCreate.toRegularPaymentEntity(
    projectEntity: ProjectEntity,
    projectReportEntity: ProjectReportEntity,
    fundEntity: ProgrammeFundEntity
) = PaymentEntity(
    type = PaymentType.REGULAR,
    project = projectEntity,
    projectCustomIdentifier = projectReportEntity.projectIdentifier,
    projectAcronym = projectReportEntity.projectAcronym,
    projectLumpSum = null,
    projectReport = projectReportEntity,
    fund = fundEntity,
    amountApprovedPerFund = amountApprovedPerFund,
)

fun PaymentFtlsToCreate.toFTLSPaymentEntity(
    projectEntity: ProjectEntity,
    lumpSum: ProjectLumpSumEntity?,
    fundEntity: ProgrammeFundEntity,
) = PaymentEntity(
    type = PaymentType.FTLS,
    project = projectEntity,
    projectCustomIdentifier = projectCustomIdentifier,
    projectAcronym = projectAcronym,
    projectLumpSum = lumpSum,
    projectReport = null,
    fund = fundEntity,
    amountApprovedPerFund = amountApprovedPerFund,
)

fun PaymentPartnerToCreate.toEntity(
    paymentEntity: PaymentEntity,
    partnerEntity: ProjectPartnerEntity,
    partnerReportEntity: ProjectPartnerReportEntity?
) = PaymentPartnerEntity(
    payment = paymentEntity,
    projectPartner = partnerEntity,
    partnerCertificate = partnerReportEntity,
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
    partnerEntity: ProjectPartnerEntity,
    partnerReportId: Long?,
    partnerReportNumber: Int?,
    installments: List<PaymentPartnerInstallment>
) = PartnerPayment(
    id = id,
    projectId = payment.project.id,
    orderNr = payment.projectLumpSum?.id?.orderNr,
    programmeLumpSumId = payment.projectLumpSum?.programmeLumpSum?.id,
    partnerReportId = partnerReportId,
    partnerReportNumber = partnerReportNumber,
    programmeFundId = payment.fund.id,
    partnerId = partnerEntity.id,
    partnerRole = partnerEntity.role,
    partnerAbbreviation = partnerEntity.abbreviation,
    partnerNumber = partnerEntity.sortNumber,
    amountApprovedPerPartner = amountApprovedPerPartner,
    installments = installments
)

// Payment Partner Installment

fun List<PaymentPartnerInstallmentEntity>.toModelList() = map { it.toDetailModel() }
fun PaymentPartnerInstallmentEntity.toDetailModel() = PaymentPartnerInstallment(
    id = id,
    fundId = paymentPartner.payment.fund.id,
    lumpSumId = paymentPartner.payment.projectLumpSum?.programmeLumpSum?.id,
    orderNr = paymentPartner.payment.projectLumpSum?.id?.orderNr,
    amountPaid = amountPaid,
    paymentDate = paymentDate,
    comment = comment,
    isSavePaymentInfo = isSavePaymentInfo,
    savePaymentInfoUser = savePaymentInfoUser?.toOutputUser(),
    savePaymentDate = savePaymentDate,
    isPaymentConfirmed = isPaymentConfirmed,
    paymentConfirmedUser = paymentConfirmedUser?.toOutputUser(),
    paymentConfirmedDate = paymentConfirmedDate,
    correction = correction?.toSimpleModel(),
)

fun PaymentPartnerInstallmentUpdate.toEntity(
    paymentPartner: PaymentPartnerEntity,
    savePaymentInfoUser: UserEntity?,
    paymentConfirmedUser: UserEntity?,
    correction: AuditControlCorrectionEntity?,
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
    paymentConfirmedDate = paymentConfirmedDate,
    correction = correction,
)

// Advance Payment

fun AdvancePaymentUpdate.toEntity(
    project: ProjectFull,
    projectVersion: String,
    partner: ProjectPartnerSummary,
    existing: AdvancePaymentEntity?,
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
    isPaymentAuthorizedInfo = existing?.isPaymentAuthorizedInfo,
    paymentAuthorizedInfoUser = existing?.paymentAuthorizedInfoUser,
    paymentAuthorizedDate = existing?.paymentAuthorizedDate,
    isPaymentConfirmed = existing?.isPaymentConfirmed,
    paymentConfirmedUser = existing?.paymentConfirmedUser,
    paymentConfirmedDate = existing?.paymentConfirmedDate
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
        partnerSortNumber = partnerSortNumber,
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

fun PaymentToCreate.toEntity(payment: PaymentEntity) = PaymentToEcExtensionEntity(
    paymentId = payment.id,
    payment = payment,
    autoPublicContribution = defaultOfWhichAutoPublic,
    correctedAutoPublicContribution = defaultOfWhichAutoPublic,
    partnerContribution = defaultPartnerContribution,
    privateContribution = defaultOfWhichPrivate,
    correctedPrivateContribution = defaultOfWhichPrivate,
    publicContribution = defaultOfWhichPublic,
    correctedPublicContribution = defaultOfWhichPublic,
    finalScoBasis = null,
)
