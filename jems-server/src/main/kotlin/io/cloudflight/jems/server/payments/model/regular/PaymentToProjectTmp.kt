package io.cloudflight.jems.server.payments.model.regular

import io.cloudflight.jems.server.payments.entity.PaymentEntity
import io.cloudflight.jems.server.payments.entity.PaymentToEcExtensionEntity
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
import java.math.BigDecimal
import java.time.LocalDate

data class PaymentToProjectTmp (
    val payment: PaymentEntity,
    val amountPaid: BigDecimal?,
    val amountAuthorized: BigDecimal?,
    val lastPaymentDate: LocalDate?,
    val totalEligibleForRegular: BigDecimal?,
    val projectFallsUnderArticle94: ContractingMonitoringExtendedOption?,
    val projectFallsUnderArticle95: ContractingMonitoringExtendedOption?,
    val code: String?,
    // nullable here can be removed as soon as also regular payments start to store contributions
    val paymentToEcExtensionEntity: PaymentToEcExtensionEntity?,
)
