package io.cloudflight.jems.server.project.service.auditAndControl.correction.measure.update

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.AuditControlCorrectionMeasure
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.AuditControlCorrectionMeasureUpdate

interface UpdateProgrammeMeasureInteractor {

    fun update(
        correctionId: Long,
        programmeMeasure: AuditControlCorrectionMeasureUpdate
    ): AuditControlCorrectionMeasure
}
