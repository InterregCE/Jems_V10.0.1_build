package io.cloudflight.jems.server.project.controller.auditAndControl.correction

import io.cloudflight.jems.api.project.auditAndControl.corrections.ProjectAuditControlCorrectionIdentificationApi
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionIdentificationDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionIdentificationUpdateDTO
import io.cloudflight.jems.server.project.controller.auditAndControl.toDto
import io.cloudflight.jems.server.project.controller.auditAndControl.toModel
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.getProjectCorrectionIdentification.GetProjectCorrectionIdentification
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.getProjectCorrectionIdentification.GetProjectPreviousClosedCorrectionsInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.updateCorrectionIdentification.UpdateProjectCorrectionIdentificationInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectAuditCorrectionIdentificationController(
    private val getProjectCorrectionIdentification: GetProjectCorrectionIdentification,
    private val getPreviousClosedCorrections: GetProjectPreviousClosedCorrectionsInteractor,
    private val updateProjectCorrectionInteractor: UpdateProjectCorrectionIdentificationInteractor
    ) : ProjectAuditControlCorrectionIdentificationApi {

    override fun getCorrectionIdentification(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long
    ): ProjectCorrectionIdentificationDTO =
        getProjectCorrectionIdentification.getProjectCorrectionIdentification(correctionId)
            .toDto()

    override fun getPreviousClosedCorrections(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long
    ): List<ProjectAuditControlCorrectionDTO> =
        getPreviousClosedCorrections.getProjectPreviousClosedCorrections(projectId, auditControlId, correctionId)
            .map { it.toDto() }

    override fun updateCorrectionIdentification(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long,
        correctionIdentificationUpdate: ProjectCorrectionIdentificationUpdateDTO
    ): ProjectCorrectionIdentificationDTO =
        updateProjectCorrectionInteractor.updateProjectAuditCorrection(
            projectId = projectId,
            auditControlId = auditControlId,
            correctionId = correctionId,
            correctionIdentificationUpdate = correctionIdentificationUpdate.toModel()
        ).toDto()

}
