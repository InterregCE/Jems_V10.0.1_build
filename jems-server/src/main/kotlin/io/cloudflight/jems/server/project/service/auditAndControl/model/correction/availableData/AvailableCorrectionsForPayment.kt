package io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection

data class AvailableCorrectionsForPayment(
    val partnerId: Long,
    val corrections: List<AuditControlCorrection>
)
