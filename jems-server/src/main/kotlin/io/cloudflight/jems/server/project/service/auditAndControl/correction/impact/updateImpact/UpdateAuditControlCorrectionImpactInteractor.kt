package io.cloudflight.jems.server.project.service.auditAndControl.correction.impact.updateImpact

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpact

interface UpdateAuditControlCorrectionImpactInteractor {

    fun update(
        correctionId: Long,
        impact: AuditControlCorrectionImpact,
    ): AuditControlCorrectionImpact

}
