package io.cloudflight.jems.server.project.service.auditAndControl.correction.programmeMeasure

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasure
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureUpdate

interface ProjectCorrectionProgrammeMeasurePersistence {

    fun getProgrammeMeasure(correctionId: Long): ProjectCorrectionProgrammeMeasure

    fun updateProgrammeMeasure(correctionId: Long, programmeMeasure: ProjectCorrectionProgrammeMeasureUpdate): ProjectCorrectionProgrammeMeasure
}
