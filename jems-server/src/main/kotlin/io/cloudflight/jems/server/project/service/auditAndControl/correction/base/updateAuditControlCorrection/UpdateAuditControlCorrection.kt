package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.updateAuditControlCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateAuditControlCorrection(
    private val auditControlCorrectionPersistence: AuditControlCorrectionPersistence,
    private val correctionValidator: CorrectionIdentificationValidator
): UpdateAuditControlCorrectionInteractor {

    @CanEditAuditControlCorrection
    @Transactional
    @ExceptionWrapper(UpdateAuditControlCorrectionException::class)
    override fun updateCorrection(
        correctionId: Long,
        data: AuditControlCorrectionUpdate,
    ): AuditControlCorrectionDetail {
        correctionValidator.validate(correctionId, data)
        return auditControlCorrectionPersistence.updateCorrection(correctionId, data)
    }

}
