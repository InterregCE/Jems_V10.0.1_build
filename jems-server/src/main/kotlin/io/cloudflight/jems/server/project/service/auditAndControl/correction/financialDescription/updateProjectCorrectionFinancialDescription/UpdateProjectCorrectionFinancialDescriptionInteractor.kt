package io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.updateProjectCorrectionFinancialDescription

import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescriptionUpdate

interface UpdateProjectCorrectionFinancialDescriptionInteractor {

    fun updateCorrectionFinancialDescription(
        projectId: Long,
        controlId: Long,
        correctionId: Long,
        correctionFinancialDescriptionUpdate: ProjectCorrectionFinancialDescriptionUpdate
    ): ProjectCorrectionFinancialDescription
}
