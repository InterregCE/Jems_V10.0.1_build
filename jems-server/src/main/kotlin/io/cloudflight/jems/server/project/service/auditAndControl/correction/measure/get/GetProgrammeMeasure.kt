package io.cloudflight.jems.server.project.service.auditAndControl.correction.measure.get

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.AuditControlCorrectionMeasure
import io.cloudflight.jems.server.project.service.auditAndControl.correction.measure.AuditControlCorrectionMeasurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProgrammeMeasure(
    private val programmeMeasurePersistence: AuditControlCorrectionMeasurePersistence,
) : GetProgrammeMeasureInteractor {

    @CanViewAuditControlCorrection
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProgrammeMeasureException::class)
    override fun get(correctionId: Long): AuditControlCorrectionMeasure =
        programmeMeasurePersistence.getProgrammeMeasure(correctionId = correctionId)
}
