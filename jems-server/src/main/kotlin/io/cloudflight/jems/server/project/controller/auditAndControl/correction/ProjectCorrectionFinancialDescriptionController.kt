package io.cloudflight.jems.server.project.controller.auditAndControl.correction

import io.cloudflight.jems.api.project.auditAndControl.corrections.ProjectAuditControlCorrectionFinancialDescriptionApi
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionFinancialDescriptionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionFinancialDescriptionUpdateDTO
import io.cloudflight.jems.server.project.controller.auditAndControl.toDto
import io.cloudflight.jems.server.project.controller.auditAndControl.toModel
import io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.getProjectCorrectionFinancialDescription.GetProjectCorrectionFinancialDescriptionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.updateProjectCorrectionFinancialDescription.UpdateProjectCorrectionFinancialDescriptionInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectCorrectionFinancialDescriptionController(
    private val getCorrectionFinancialDescription: GetProjectCorrectionFinancialDescriptionInteractor,
    private val updateCorrectionFinancialDescription: UpdateProjectCorrectionFinancialDescriptionInteractor
): ProjectAuditControlCorrectionFinancialDescriptionApi {

    override fun getCorrectionFinancialDescription(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long
    ): ProjectCorrectionFinancialDescriptionDTO =
        getCorrectionFinancialDescription.getCorrectionFinancialDescription(correctionId).toDto()

    override fun updateCorrectionFinancialDescription(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long,
        financialDescriptionUpdate: ProjectCorrectionFinancialDescriptionUpdateDTO
    ): ProjectCorrectionFinancialDescriptionDTO =
        updateCorrectionFinancialDescription.updateCorrectionFinancialDescription(
            projectId,
            auditControlId,
            correctionId,
            financialDescriptionUpdate.toModel()
        ).toDto()
}
