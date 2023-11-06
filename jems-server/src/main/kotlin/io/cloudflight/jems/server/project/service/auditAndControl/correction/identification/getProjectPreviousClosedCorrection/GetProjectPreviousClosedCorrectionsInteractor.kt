package io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.getProjectCorrectionIdentification

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection

interface GetProjectPreviousClosedCorrectionsInteractor {

    fun getProjectPreviousClosedCorrections(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long
    ): List<ProjectAuditControlCorrection>

}
