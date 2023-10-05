package io.cloudflight.jems.server.project.service.auditAndControl.updateProjectAudit

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectAuditAndControl
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlPersistenceProvider
import io.cloudflight.jems.server.project.repository.auditAndControl.toUpdatedModel
import io.cloudflight.jems.server.project.service.auditAndControl.ProjectAuditAndControlValidator
import io.cloudflight.jems.server.project.service.auditAndControl.createProjectAudit.AuditControlClosedException
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControlUpdate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectAuditControl(
    private val auditControlPersistence: AuditControlPersistenceProvider,
    private val projectAuditAndControlValidator: ProjectAuditAndControlValidator
) : UpdateProjectAuditControlInteractor {


    @CanEditProjectAuditAndControl
    @Transactional
    @ExceptionWrapper(UpdateProjectAuditControlException::class)
    override fun updateAudit(projectId: Long, auditControlId: Long, auditControlData: ProjectAuditControlUpdate
    ): ProjectAuditControl {

        projectAuditAndControlValidator.validateData(auditControlData)

        val existingAuditControl =
            auditControlPersistence.findByIdAndProjectId(auditControlId = auditControlId, projectId = projectId)
        validateAuditControlStatus(existingAuditControl)

        return auditControlPersistence.saveAuditControl(
            auditControl = existingAuditControl.toUpdatedModel(auditControlData)
        )
    }

    private fun validateAuditControlStatus(existingAuditControl: ProjectAuditControl){
        if (existingAuditControl.status == AuditStatus.Closed) {
            throw AuditControlClosedException()
        }
    }
}
