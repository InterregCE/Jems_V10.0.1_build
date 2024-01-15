package io.cloudflight.jems.server.payments.model.ec

import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption

data class CorrectionInEcPaymentMetadata(
    val correctionId: Long,
    val auditControlNr: Int,
    val correctionNr: Int,
    val projectId: Long,
    val typologyProv94: ContractingMonitoringExtendedOption?,
    val typologyProv95: ContractingMonitoringExtendedOption?,
)
