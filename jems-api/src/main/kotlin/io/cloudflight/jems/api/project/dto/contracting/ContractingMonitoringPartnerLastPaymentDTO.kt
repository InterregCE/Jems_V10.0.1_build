package io.cloudflight.jems.api.project.dto.contracting

import java.time.LocalDate

data class ContractingMonitoringPartnerLastPaymentDTO(
    val id: Long,
    val projectId: Long,
    val partnerId: Long,
    val lastPaymentDate: LocalDate? = null,
)
