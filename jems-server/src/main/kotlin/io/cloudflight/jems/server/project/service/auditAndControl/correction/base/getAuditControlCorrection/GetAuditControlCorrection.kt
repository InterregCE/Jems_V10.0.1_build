package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.getAuditControlCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAuditControlCorrection(
    private val correctionPersistence: AuditControlCorrectionPersistence,
): GetAuditControlCorrectionInteractor {

    @CanViewAuditControlCorrection
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAuditControlCorrectionException::class)
    override fun getCorrection(correctionId: Long): AuditControlCorrectionDetail =
        correctionPersistence.getByCorrectionId(correctionId)

}
