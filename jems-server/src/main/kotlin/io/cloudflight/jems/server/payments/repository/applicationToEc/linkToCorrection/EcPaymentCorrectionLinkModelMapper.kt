package io.cloudflight.jems.server.payments.repository.applicationToEc.linkToCorrection

import io.cloudflight.jems.server.payments.entity.PaymentToEcCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.model.ec.EcPaymentCorrectionExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinking
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionTmp
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.toSimpleModel
import java.math.BigDecimal


fun PaymentToEcCorrectionExtensionEntity.toModel() = EcPaymentCorrectionExtension(
    correctionId = correction.id,
    ecPaymentId = paymentApplicationToEc?.id,
    ecPaymentStatus = paymentApplicationToEc?.status,
    comment = comment,
    fundAmount = fundAmount,
    publicContribution = publicContribution,
    correctedPublicContribution = correctedPublicContribution,
    autoPublicContribution= autoPublicContribution,
    correctedAutoPublicContribution = correctedAutoPublicContribution,
    privateContribution = privateContribution,
    correctedPrivateContribution = correctedPrivateContribution,
    auditControlStatus = correction.auditControl.status
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
    partnerContribution = partnerContribution,
    publicContribution = publicContribution,
    correctedPublicContribution = correctedPublicContribution,
    autoPublicContribution = autoPublicContribution,
    correctedAutoPublicContribution = correctedAutoPublicContribution,
    privateContribution = privateContribution,
    correctedPrivateContribution = correctedPrivateContribution,
    comment = comment
)

