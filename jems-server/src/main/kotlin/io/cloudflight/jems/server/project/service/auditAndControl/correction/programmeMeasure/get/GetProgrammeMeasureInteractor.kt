package io.cloudflight.jems.server.project.service.auditAndControl.correction.programmeMeasure.get

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasure

interface GetProgrammeMeasureInteractor {

    fun get(correctionId: Long): ProjectCorrectionProgrammeMeasure
}
