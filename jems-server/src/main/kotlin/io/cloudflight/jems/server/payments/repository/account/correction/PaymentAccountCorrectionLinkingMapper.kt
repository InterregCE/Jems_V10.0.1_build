package io.cloudflight.jems.server.payments.repository.account.correction

import io.cloudflight.jems.server.payments.entity.account.PaymentAccountPriorityAxisOverviewEntity
import io.cloudflight.jems.server.payments.entity.account.PaymentAccountCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.model.account.PaymentAccountAmountSummaryLine
import io.cloudflight.jems.server.payments.model.account.PaymentAccountCorrectionExtension
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(PaymentAccountCorrectionLinkingMapper::class.java)

fun PaymentAccountCorrectionExtensionEntity.toModel() = PaymentAccountCorrectionExtension(
    correctionId = correction.id,
    paymentAccountId = paymentAccount?.id,
    paymentAccountStatus = paymentAccount?.status,
    comment = comment,
    fundAmount = fundAmount,
    correctedFundAmount = correctedFundAmount,
    publicContribution = publicContribution,
    correctedPublicContribution = correctedPublicContribution,
    autoPublicContribution= autoPublicContribution,
    correctedAutoPublicContribution = correctedAutoPublicContribution,
    privateContribution = privateContribution,
    correctedPrivateContribution = correctedPrivateContribution,
    auditControlStatus = correction.auditControl.status,
)

fun ProjectCorrectionFinancialDescription.toEntity(
    correctionEntity: AuditControlCorrectionEntity,
) = PaymentAccountCorrectionExtensionEntity(
    correctionId = correctionId,
    correction = correctionEntity,
    paymentAccount = null,
    fundAmount = fundAmount,
    correctedFundAmount = fundAmount,
    publicContribution = publicContribution,
    correctedPublicContribution = publicContribution,
    autoPublicContribution = autoPublicContribution,
    correctedAutoPublicContribution = autoPublicContribution,
    privateContribution = privateContribution,
    correctedPrivateContribution = privateContribution,
    comment = null,
    finalScoBasis = null,
)

fun List<PaymentAccountPriorityAxisOverviewEntity>.toModel() =
    associate { Pair(it.priorityAxis?.id, mapper.map(it)) }

@Mapper
interface PaymentAccountCorrectionLinkingMapper {

    @Mapping(source = "priorityAxis.code", target = "priorityAxis")
    fun map(entity: PaymentAccountPriorityAxisOverviewEntity): PaymentAccountAmountSummaryLine

}
