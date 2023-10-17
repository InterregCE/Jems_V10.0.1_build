package io.cloudflight.jems.server.project.service.auditAndControl.correction.model

data class ProjectAuditControlCorrectionExtended(
    val correction: ProjectAuditControlCorrection,
    val auditControlNumber: Int,
    val projectCustomIdentifier: String
)
