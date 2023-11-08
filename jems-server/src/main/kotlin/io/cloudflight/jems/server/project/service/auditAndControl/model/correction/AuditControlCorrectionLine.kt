package io.cloudflight.jems.server.project.service.auditAndControl.model.correction

import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus

data class AuditControlCorrectionLine(
    val id: Long,
    val orderNr: Int,
    val status: AuditControlStatus,
    val type: AuditControlCorrectionType,

    val auditControlId: Long,
    val auditControlNr: Int,

    val canBeDeleted: Boolean,
)
