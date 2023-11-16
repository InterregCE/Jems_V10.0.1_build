package io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact

data class AuditControlCorrectionImpact(
    val action: CorrectionImpactAction,
    val comment: String,
)
