package io.cloudflight.jems.server.project.service.auditAndControl

import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl

interface AuditControlPersistence {

    fun saveAuditControl(auditControl: ProjectAuditControl): ProjectAuditControl

    fun findByIdAndProjectId(auditControlId: Long, projectId: Long ): ProjectAuditControl

    fun findAllProjectAudits(projectId: Long): List<ProjectAuditControl>

    fun updateProjectAuditStatus( projectId: Long, auditControlId: Long, auditStatus: AuditStatus): ProjectAuditControl

    fun countAuditsForProject(projectId: Long): Long
}