package io.cloudflight.jems.server.project.service.auditAndControl.updateProjectAudit

import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControlUpdate

interface UpdateProjectAuditControlInteractor {

    fun updateAudit(projectId: Long, auditControlId: Long, auditControlData: ProjectAuditControlUpdate): ProjectAuditControl
}
