package io.cloudflight.jems.server.project.service.auditAndControl

import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlCreate
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlUpdate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface AuditControlPersistence {

    fun getProjectIdForAuditControl(auditControlId: Long): Long

    fun createControl(projectId: Long, auditControl: AuditControlCreate): AuditControl

    fun updateControl(auditControlId: Long, auditControl: AuditControlUpdate): AuditControl

    fun getById(auditControlId: Long): AuditControl

    fun findAllProjectAudits(projectId: Long, pageable: Pageable): Page<AuditControl>

    fun updateAuditControlStatus(auditControlId: Long, status: AuditControlStatus): AuditControl

    fun countAuditsForProject(projectId: Long): Int

}
