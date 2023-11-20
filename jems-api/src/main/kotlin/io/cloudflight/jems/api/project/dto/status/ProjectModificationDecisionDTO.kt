package io.cloudflight.jems.api.project.dto.status

import io.cloudflight.jems.api.project.dto.auditAndControl.correction.AuditControlCorrectionDTO

data class ProjectModificationDecisionDTO(
    val projectStatus: ProjectStatusDTO,
    val corrections: List<AuditControlCorrectionDTO>
)
