package io.cloudflight.jems.server.project.service.auditAndControl.correction.listPreviouslyClosedCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListPreviouslyClosedCorrection(
    private val correctionPersistence: AuditControlCorrectionPersistence,
): ListPreviouslyClosedCorrectionInteractor {

    @CanViewAuditControlCorrection
    @Transactional
    @ExceptionWrapper(ListPreviouslyClosedCorrectionException::class)
    override fun getClosedCorrectionsBefore(correctionId: Long): List<AuditControlCorrection> =
        correctionPersistence.getPreviousClosedCorrections(correctionId)

}
