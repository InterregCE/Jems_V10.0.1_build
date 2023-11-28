package io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection

data class AvailableCorrectionsForPayment(
    val partnerId: Long,
    val corrections: List<AuditControlCorrection>
)
