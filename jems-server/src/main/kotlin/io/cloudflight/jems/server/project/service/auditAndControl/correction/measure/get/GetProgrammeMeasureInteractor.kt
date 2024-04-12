package io.cloudflight.jems.server.project.service.auditAndControl.correction.measure.get

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.AuditControlCorrectionMeasure

interface GetProgrammeMeasureInteractor {

    fun get(correctionId: Long): AuditControlCorrectionMeasure
}
