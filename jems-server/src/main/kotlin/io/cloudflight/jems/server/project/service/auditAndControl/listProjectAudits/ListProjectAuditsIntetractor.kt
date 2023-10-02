package io.cloudflight.jems.server.project.service.auditAndControl.listProjectAudits

import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl

interface ListProjectAuditsIntetractor {

    fun listForProject(projectId: Long): List<ProjectAuditControl>
}