package io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.getProjectCorrectionIdentification

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentification


interface GetProjectCorrectionIdentificationInteractor {

    fun getProjectCorrectionIdentification(
        correctionId: Long
    ): ProjectCorrectionIdentification

}
