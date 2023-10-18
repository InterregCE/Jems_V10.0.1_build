package io.cloudflight.jems.server.project.service.auditAndControl.getProjectAuditDetails

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewProjectAuditAndControl
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlPersistenceProvider
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectAuditControlDetails(
    private val auditControlPersistence: AuditControlPersistenceProvider
): GetProjectAuditControlDetailsInteractor {

    @CanViewProjectAuditAndControl
    @Transactional
    @ExceptionWrapper(GetProjectAuditDetailsException::class)
    override fun getDetails(projectId: Long, auditId: Long): ProjectAuditControl {
        return auditControlPersistence.getByIdAndProjectId(projectId = projectId, auditControlId = auditId) ?: throw AuditControlNotFound()
    }
}
