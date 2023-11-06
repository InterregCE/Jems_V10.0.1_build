package io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.updateCorrectionIdentification

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentification
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentificationUpdate

interface UpdateProjectCorrectionIdentificationInteractor {

    fun updateProjectAuditCorrection(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long,
        correctionIdentificationUpdate: ProjectCorrectionIdentificationUpdate
    ): ProjectCorrectionIdentification

}
