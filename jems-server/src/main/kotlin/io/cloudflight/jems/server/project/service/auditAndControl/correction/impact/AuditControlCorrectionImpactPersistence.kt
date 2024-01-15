package io.cloudflight.jems.server.project.service.auditAndControl.correction.impact

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpact

interface AuditControlCorrectionImpactPersistence {

    fun updateCorrectionImpact(
        correctionId: Long,
        impact: AuditControlCorrectionImpact
    ): AuditControlCorrectionImpact

}
