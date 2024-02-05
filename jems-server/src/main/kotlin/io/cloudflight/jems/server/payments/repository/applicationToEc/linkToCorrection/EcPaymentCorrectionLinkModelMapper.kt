package io.cloudflight.jems.server.payments.repository.applicationToEc.linkToCorrection

import io.cloudflight.jems.server.payments.entity.PaymentToEcCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinking
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionTmp
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.toSimpleModel
import java.math.BigDecimal


fun PaymentToEcCorrectionExtensionEntity.toModel() = PaymentToEcCorrectionExtension(
    correctionId = correction.id,
    ecPaymentId = paymentApplicationToEc?.id,
    ecPaymentStatus = paymentApplicationToEc?.status,
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
    totalEligibleWithoutArt94or95 = totalEligibleWithoutArt94or95,
    correctedTotalEligibleWithoutArt94or95 = correctedTotalEligibleWithoutArt94or95,
    unionContribution = unionContribution,
    correctedUnionContribution = correctedUnionContribution,
)

fun PaymentToEcCorrectionTmp.toModel(partnerContribution: BigDecimal) = PaymentToEcCorrectionLinking(
    correction = correctionEntity.toSimpleModel(),

    projectId = projectId,
    projectAcronym = projectAcronym,
    projectCustomIdentifier = projectCustomIdentifier,
    priorityAxis = priorityAxis ?: "N/A",
    controllingBody = controllingBody,
    scenario = scenario,
    projectFlagged94Or95 = isProjectFlagged94Or95,
    paymentToEcId = paymentToEcId,

    fundAmount = fundAmount,
    correctedFundAmount = correctedFundAmount,
    partnerContribution = partnerContribution,
    publicContribution = publicContribution,
    correctedPublicContribution = correctedPublicContribution,
    autoPublicContribution = autoPublicContribution,
    correctedAutoPublicContribution = correctedAutoPublicContribution,
    privateContribution = privateContribution,
    correctedPrivateContribution = correctedPrivateContribution,
    comment = comment,

    totalEligibleWithoutArt94or95 = totalEligibleWithoutArt94or95,
    correctedTotalEligibleWithoutArt94or95 = correctedTotalEligibleWithoutArt94or95,
    unionContribution = unionContribution,
    correctedUnionContribution = correctedUnionContribution
)

