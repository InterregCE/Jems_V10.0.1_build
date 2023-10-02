package io.cloudflight.jems.server.project.service.auditAndControl.listProjectAudits

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewProjectAuditAndControl
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlPersistenceProvider
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListProjectAudits(
    private val auditControlPersistence: AuditControlPersistenceProvider
): ListProjectAuditsIntetractor {

    @CanViewProjectAuditAndControl
    @Transactional
    @ExceptionWrapper(ListProjectAuditControlException::class)
    override fun listForProject(projectId: Long): List<ProjectAuditControl> {
        return auditControlPersistence.findAllProjectAudits(projectId)
    }
}