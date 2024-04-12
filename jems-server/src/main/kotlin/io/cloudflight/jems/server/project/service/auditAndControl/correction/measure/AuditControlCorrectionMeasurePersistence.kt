package io.cloudflight.jems.server.project.service.auditAndControl.correction.measure

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.AuditControlCorrectionMeasure
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.AuditControlCorrectionMeasureUpdate

interface AuditControlCorrectionMeasurePersistence {

    fun getProgrammeMeasure(correctionId: Long): AuditControlCorrectionMeasure

    fun updateProgrammeMeasure(correctionId: Long, programmeMeasure: AuditControlCorrectionMeasureUpdate): AuditControlCorrectionMeasure
}
