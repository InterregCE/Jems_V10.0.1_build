package io.cloudflight.jems.server.project.service.auditAndControl.base.closeAuditControl

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanCloseAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.projectAuditControlClosed
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CloseAuditControl(
    private val auditControlPersistence: AuditControlPersistence,
    private val auditControlCorrectionPersistence: AuditControlCorrectionPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : CloseAuditControlInteractor {

    @CanCloseAuditControl
    @Transactional
    @ExceptionWrapper(CloseProjectAuditControlException::class)
    override fun closeAuditControl(auditControlId: Long): AuditControlStatus {
        val auditControl = auditControlPersistence.getById(auditControlId = auditControlId)

        validateAuditControlNotClosed(auditControl)
        validateNoAnyOngoingCorrectionsExist(auditControlId)

        return auditControlPersistence.updateAuditControlStatus(
            auditControlId = auditControlId, status = AuditControlStatus.Closed,
        ).also {
            auditPublisher.publishEvent(projectAuditControlClosed(this, it))
        }.status
    }

    private fun validateAuditControlNotClosed(auditControl: AuditControl) {
        if (auditControl.status.isClosed())
            throw AuditControlClosedException()
    }

    private fun validateNoAnyOngoingCorrectionsExist(auditControlId: Long) {
        val ongoingCorrections = auditControlCorrectionPersistence.getOngoingCorrectionsByAuditControlId(auditControlId)
        if (ongoingCorrections.isNotEmpty())
            throw CorrectionsStillOpenException()
    }

}
