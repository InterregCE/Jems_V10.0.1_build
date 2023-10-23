package io.cloudflight.jems.api.project.dto.auditAndControl.correction

data class ProjectAuditControlCorrectionDTO(
    val id: Long,
    val auditControlId: Long,
    val orderNr: Int,
    val status: CorrectionStatusDTO,
    val linkedToInvoice: Boolean,
)
