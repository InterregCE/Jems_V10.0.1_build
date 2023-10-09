package io.cloudflight.jems.server.project.service.auditAndControl.listProjectAudits

import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ListProjectAuditsIntetractor {

    fun listForProject(projectId: Long, pageable: Pageable): Page<ProjectAuditControl>
}
