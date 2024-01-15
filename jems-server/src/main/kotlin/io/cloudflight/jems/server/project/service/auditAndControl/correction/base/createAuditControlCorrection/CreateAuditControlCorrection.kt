package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.createAuditControlCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCreateCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionCreate
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionFollowUpType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.CorrectionImpactAction
import io.cloudflight.jems.server.project.service.projectAuditControlCorrectionCreated
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateAuditControlCorrection(
    private val auditControlPersistence: AuditControlPersistence,
    private val auditControlCorrectionPersistence: AuditControlCorrectionPersistence,
    private val createCorrectionPersistence: AuditControlCreateCorrectionPersistence,
    private val auditPublisher: ApplicationEventPublisher,
): CreateAuditControlCorrectionInteractor {

    @CanEditAuditControl
    @Transactional
    @ExceptionWrapper(CreateAuditControlCorrectionException::class)
    override fun createCorrection(auditControlId: Long, type: AuditControlCorrectionType): AuditControlCorrectionDetail {
        val auditControl = auditControlPersistence.getById(auditControlId)
        validateAuditControlNotClosed(auditControl)

        val lastUsedOrderNr = auditControlCorrectionPersistence.getLastUsedOrderNr(auditControlId) ?: 0
        validateMaxAmountOfCorrections(lastUsedOrderNr)

        val toCreate = AuditControlCorrectionCreate(
            orderNr = lastUsedOrderNr + 1,
            status = AuditControlStatus.Ongoing,
            type = type,
            followUpOfCorrectionType = CorrectionFollowUpType.No,
            defaultImpact = CorrectionImpactAction.NA,
        )

        return createCorrectionPersistence.createCorrection(auditControlId = auditControlId, toCreate).also {
            auditPublisher.publishEvent(
                projectAuditControlCorrectionCreated(this, auditControl, correctionNr = it.orderNr)
            )
        }
    }

    private fun validateAuditControlNotClosed(auditControl: AuditControl) {
        if (auditControl.status.isClosed())
            throw AuditControlClosedException()
    }

    private fun validateMaxAmountOfCorrections(lastCorrectionNumber: Int) {
        if (lastCorrectionNumber >= 100)
            throw MaximumNumberOfCorrectionsException()
    }

}
