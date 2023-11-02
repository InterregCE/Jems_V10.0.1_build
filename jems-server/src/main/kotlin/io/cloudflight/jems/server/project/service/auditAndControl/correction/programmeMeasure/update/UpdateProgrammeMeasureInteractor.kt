package io.cloudflight.jems.server.project.service.auditAndControl.correction.programmeMeasure.update

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasure
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureUpdate

interface UpdateProgrammeMeasureInteractor {

    fun update(
        correctionId: Long,
        programmeMeasure: ProjectCorrectionProgrammeMeasureUpdate
    ): ProjectCorrectionProgrammeMeasure
}
