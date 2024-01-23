package io.cloudflight.jems.server.project.service.contracting.model

import java.time.LocalDate

data class ContractingMonitoringPartnerLastPayment(
    val id: Long,
    val projectId: Long,
    val partnerId: Long,
    val lastPaymentDate: LocalDate? = null,
)
