package io.cloudflight.jems.server.project.service.auditAndControl.correction.impact.updateImpact

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.impact.AuditControlCorrectionImpactPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpact
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateAuditControlCorrectionImpact(
    private val auditControlPersistence: AuditControlPersistence,
    private val auditControlCorrectionPersistence: AuditControlCorrectionPersistence,
    private val impactPersistence: AuditControlCorrectionImpactPersistence,
): UpdateAuditControlCorrectionImpactInteractor {

    @CanEditAuditControlCorrection
    @Transactional
    @ExceptionWrapper(UpdateAuditControlCorrectionImpactException::class)
    override fun update(
        correctionId: Long,
        impact: AuditControlCorrectionImpact,
    ): AuditControlCorrectionImpact {
        val correction = auditControlCorrectionPersistence.getByCorrectionId(correctionId)
        val auditControl = auditControlPersistence.getById(correction.auditControlId)

        validateAuditControlNotClosed(auditControl)
        validateAuditControlCorrectionNotClosed(correction)

        return impactPersistence.updateCorrectionImpact(correctionId, impact)
    }

    private fun validateAuditControlNotClosed(auditControl: AuditControl) {
        if (auditControl.status.isClosed())
            throw AuditControlClosedException()
    }

    private fun validateAuditControlCorrectionNotClosed(correction: AuditControlCorrectionDetail) {
        if (correction.status.isClosed())
            throw AuditControlCorrectionClosedException()
    }

}
