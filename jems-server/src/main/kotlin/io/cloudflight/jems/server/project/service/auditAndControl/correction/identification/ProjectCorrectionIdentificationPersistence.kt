package io.cloudflight.jems.server.project.service.auditAndControl.correction.identification

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentification
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentificationUpdate


interface ProjectCorrectionIdentificationPersistence {

    fun getCorrectionIdentification(correctionId: Long): ProjectCorrectionIdentification

    fun updateCorrectionIdentification(
        correctionId: Long,
        correctionIdentificationUpdate: ProjectCorrectionIdentificationUpdate
    ): ProjectCorrectionIdentification

}
