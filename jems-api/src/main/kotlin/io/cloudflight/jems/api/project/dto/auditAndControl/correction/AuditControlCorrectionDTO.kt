package io.cloudflight.jems.api.project.dto.auditAndControl.correction

import io.cloudflight.jems.api.project.dto.auditAndControl.AuditStatusDTO

data class AuditControlCorrectionDTO(
    val id: Long,
    val orderNr: Int,
    val status: AuditStatusDTO,
    val type: AuditControlCorrectionTypeDTO,

    val auditControlId: Long,
    val auditControlNumber: Int,
)
