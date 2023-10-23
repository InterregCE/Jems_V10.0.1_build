package io.cloudflight.jems.server.project.service.auditAndControl.correction.listProjectAuditCorrection

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrectionLine
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ListProjectAuditControlCorrectionsInteractor {

    fun listProjectAuditCorrections(projectId: Long, auditControlId: Long, pageable: Pageable): Page<ProjectAuditControlCorrectionLine>

}
