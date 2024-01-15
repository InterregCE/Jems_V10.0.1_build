package io.cloudflight.jems.server.project.service.auditAndControl.base.reopenAuditControl

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanReopenAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.projectAuditControlReopened
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReopenAuditControl(
    private val auditControlPersistence: AuditControlPersistence,
    private val auditPublisher: ApplicationEventPublisher
) : ReopenAuditControlInteractor {

    @CanReopenAuditControl
    @Transactional
    @ExceptionWrapper(ReopenProjectAuditControlException::class)
    override fun reopenAuditControl(auditControlId: Long): AuditControlStatus {
        val auditControl = auditControlPersistence.getById(auditControlId = auditControlId)
        validateAuditControlNotClosed(auditControl)

        return auditControlPersistence.updateAuditControlStatus(
            auditControlId = auditControlId, status = AuditControlStatus.Ongoing,
        ).also {
            auditPublisher.publishEvent(projectAuditControlReopened(this, it))
        }.status
    }

    private fun validateAuditControlNotClosed(auditControl: AuditControl) {
        if (!auditControl.status.isClosed())
            throw AuditControlNotClosedException()
    }
}
