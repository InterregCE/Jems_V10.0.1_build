package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection

data class ProjectModificationDecision(
    val projectStatus: ProjectStatus,
    val corrections: List<AuditControlCorrection>
)
