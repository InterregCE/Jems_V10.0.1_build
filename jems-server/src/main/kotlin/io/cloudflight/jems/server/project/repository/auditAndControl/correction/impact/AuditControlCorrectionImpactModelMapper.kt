package io.cloudflight.jems.server.project.repository.auditAndControl.correction.impact

import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpact

fun AuditControlCorrectionEntity.toImpactModel() = AuditControlCorrectionImpact(
    action = impact,
    comment = impactComment,
)
