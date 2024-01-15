package io.cloudflight.jems.server.payments.model.ec

import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption

data class PaymentInEcPaymentMetadata(
    val paymentId: Long,
    val type: PaymentType,
    val typologyProv94: ContractingMonitoringExtendedOption?,
    val typologyProv95: ContractingMonitoringExtendedOption?,
)
