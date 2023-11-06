package io.cloudflight.jems.server.project.controller.auditAndControl.correction

import io.cloudflight.jems.api.project.auditAndControl.corrections.ProjectAuditControlCorrectionApi
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.CorrectionStatusDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionExtendedDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionLineDTO
import io.cloudflight.jems.server.project.controller.auditAndControl.toDto
import io.cloudflight.jems.server.project.service.auditAndControl.correction.closeProjectAuditCorrection.CloseProjectAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.createProjectAuditCorrection.CreateProjectAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.deleteProjectAuditCorrection.DeleteProjectAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.getProjectAuditCorrection.GetProjectAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.listProjectAuditCorrection.ListProjectAuditControlCorrectionsInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectAuditCorrectionController(
    private val createProjectCorrection: CreateProjectAuditControlCorrectionInteractor,
    private val closeProjectCorrection: CloseProjectAuditControlCorrectionInteractor,
    private val getProjectAuditCorrection: GetProjectAuditControlCorrectionInteractor,
    private val listProjectAuditCorrections: ListProjectAuditControlCorrectionsInteractor,
    private val deleteProjectAuditCorrection: DeleteProjectAuditControlCorrectionInteractor,
) : ProjectAuditControlCorrectionApi {

    override fun createProjectAuditCorrection(
        projectId: Long,
        auditControlId: Long,
        linkedToInvoice: Boolean
    ): ProjectAuditControlCorrectionDTO =
        createProjectCorrection.createProjectAuditCorrection(projectId, auditControlId, linkedToInvoice).toDto()

    override fun listProjectAuditCorrections(
        projectId: Long,
        auditControlId: Long,
        pageable: Pageable
    ): Page<ProjectAuditControlCorrectionLineDTO> =
        listProjectAuditCorrections.listProjectAuditCorrections(projectId, auditControlId, pageable).map { it.toDto() }

    override fun deleteProjectAuditCorrection(projectId: Long, auditControlId: Long, correctionId: Long) =
        deleteProjectAuditCorrection.deleteProjectAuditCorrection(projectId, auditControlId, correctionId)

    override fun getProjectAuditCorrection(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long
    ): ProjectAuditControlCorrectionExtendedDTO =
        getProjectAuditCorrection.getProjectAuditCorrection(
            correctionId = correctionId
        ).toDto()

    override fun closeProjectCorrection(
        projectId: Long,
        auditControlId: Long,
        correctionId: Long
    ): CorrectionStatusDTO =
        closeProjectCorrection.closeProjectAuditCorrection(projectId, auditControlId, correctionId).toDto()

}
