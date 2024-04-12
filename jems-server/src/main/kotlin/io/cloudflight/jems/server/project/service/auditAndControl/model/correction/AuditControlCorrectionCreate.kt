package io.cloudflight.jems.server.project.service.auditAndControl.model.correction

import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpactAction

data class AuditControlCorrectionCreate(
    val orderNr: Int,
    val status: AuditControlStatus,
    val type: AuditControlCorrectionType,
    val followUpOfCorrectionType: CorrectionFollowUpType,
    val defaultImpact: AuditControlCorrectionImpactAction,
)
