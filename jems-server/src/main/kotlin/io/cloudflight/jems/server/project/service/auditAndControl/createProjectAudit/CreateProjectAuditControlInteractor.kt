package io.cloudflight.jems.server.project.service.auditAndControl.createProjectAudit

import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControlUpdate

interface CreateProjectAuditControlInteractor {

    fun createAudit(projectId: Long, auditControl: ProjectAuditControlUpdate): ProjectAuditControl
}