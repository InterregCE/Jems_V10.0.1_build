package io.cloudflight.jems.server.project.service.auditAndControl.correction.model

data class ProjectAuditControlCorrection(
    val id: Long,
    val auditControlId: Long,
    val orderNr: Int,
    val status: CorrectionStatus,
    val linkedToInvoice: Boolean,
)
