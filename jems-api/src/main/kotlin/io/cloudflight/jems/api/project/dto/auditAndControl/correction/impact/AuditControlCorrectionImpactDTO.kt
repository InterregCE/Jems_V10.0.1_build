package io.cloudflight.jems.api.project.dto.auditAndControl.correction.impact

data class AuditControlCorrectionImpactDTO(
    val action: CorrectionImpactActionDTO,
    val comment: String,
)
