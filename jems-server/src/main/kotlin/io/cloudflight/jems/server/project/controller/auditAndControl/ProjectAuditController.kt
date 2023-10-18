package io.cloudflight.jems.server.project.controller.auditAndControl

import io.cloudflight.jems.api.project.auditAndControl.ProjectAuditAndControlApi
import io.cloudflight.jems.api.project.dto.auditAndControl.AuditControlDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.AuditStatusDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.ProjectAuditControlUpdateDTO
import io.cloudflight.jems.server.project.service.auditAndControl.closeProjectAudit.CloseProjectAuditControlInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.createProjectAudit.CreateProjectAuditControlInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.getProjectAuditDetails.GetProjectAuditControlDetailsInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.listProjectAudits.ListProjectAuditsIntetractor
import io.cloudflight.jems.server.project.service.auditAndControl.updateProjectAudit.UpdateProjectAuditControlInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectAuditController(
    private val createAuditControlInteractor: CreateProjectAuditControlInteractor,
    private val updateProjectAuditControlInteractor: UpdateProjectAuditControlInteractor,
    private val listProjectAuditsInteractor: ListProjectAuditsIntetractor,
    private val getAuditDetailsInteractor: GetProjectAuditControlDetailsInteractor,
    private val closeProjectAuditControlInteractor: CloseProjectAuditControlInteractor,
): ProjectAuditAndControlApi {

    override fun createProjectAudit(projectId: Long, auditData: ProjectAuditControlUpdateDTO): AuditControlDTO {
       return  createAuditControlInteractor.createAudit(projectId, auditData.toModel()).toDto()
    }

    override fun updateProjectAudit(
        projectId: Long,
        auditControlId: Long,
        auditData: ProjectAuditControlUpdateDTO
    ): AuditControlDTO {
       return updateProjectAuditControlInteractor.updateAudit(
            projectId = projectId,
            auditControlId = auditControlId,
            auditControlData = auditData.toModel()
        ).toDto()
    }

    override fun listAuditsForProject(projectId: Long, pageable: Pageable): Page<AuditControlDTO> =
        listProjectAuditsInteractor.listForProject(projectId, pageable).map { it.toDto() }

    override fun getAuditDetail(projectId: Long, auditControlId: Long): AuditControlDTO =
        getAuditDetailsInteractor.getDetails(projectId = projectId, auditId = auditControlId).toDto()

    override fun closeAuditControl(projectId: Long, auditControlId: Long): AuditStatusDTO =
        closeProjectAuditControlInteractor.closeAuditControl(projectId = projectId, auditControlId = auditControlId).toDto()

}
