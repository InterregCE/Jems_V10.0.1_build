package io.cloudflight.jems.server.project.service.auditAndControl.base.updateAuditControl

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditAuditControl
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlPersistenceProvider
import io.cloudflight.jems.server.project.service.auditAndControl.validator.ProjectAuditAndControlValidator
import io.cloudflight.jems.server.project.service.auditAndControl.base.createAuditControl.AuditControlClosedException
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlUpdate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateAuditControl(
    private val auditControlPersistence: AuditControlPersistenceProvider,
    private val projectAuditAndControlValidator: ProjectAuditAndControlValidator
) : UpdateAuditControlInteractor {

    @CanEditAuditControl
    @Transactional
    @ExceptionWrapper(UpdateAuditControlException::class)
    override fun updateAudit(auditControlId: Long, auditControlData: AuditControlUpdate): AuditControl {
        projectAuditAndControlValidator.validateData(auditControlData)

        val auditControl = auditControlPersistence.getById(auditControlId)
        validateAuditControlNotClosed(auditControl)

        return auditControlPersistence.updateControl(auditControlId, auditControlData)
    }

    private fun validateAuditControlNotClosed(auditControl: AuditControl) {
        if (auditControl.status.isClosed())
            throw AuditControlClosedException()
    }

}
