package io.cloudflight.jems.server.payments.model.regular

import io.cloudflight.jems.server.payments.entity.PaymentEntity
import io.cloudflight.jems.server.payments.entity.PaymentToEcExtensionEntity
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption

data class PaymentToEcExtensionTmp(
    val payment: PaymentEntity,
    val projectFallsUnderArticle94: ContractingMonitoringExtendedOption?,
    val projectFallsUnderArticle95: ContractingMonitoringExtendedOption?,
    val code: String?,
    // nullable here can be removed as soon as also regular payments start to store contributions
    val paymentToEcExtensionEntity: PaymentToEcExtensionEntity,
    )
