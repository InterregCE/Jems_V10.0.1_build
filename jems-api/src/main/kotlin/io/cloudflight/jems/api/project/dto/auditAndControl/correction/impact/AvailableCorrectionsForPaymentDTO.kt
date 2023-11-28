package io.cloudflight.jems.api.project.dto.auditAndControl.correction.impact

import io.cloudflight.jems.api.project.dto.auditAndControl.correction.AuditControlCorrectionDTO

data class AvailableCorrectionsForPaymentDTO(
    val partnerId: Long,
    val corrections: List<AuditControlCorrectionDTO>,
)
