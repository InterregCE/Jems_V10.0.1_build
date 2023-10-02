package io.cloudflight.jems.server.project.controller.auditAndControl

import io.cloudflight.jems.api.project.auditAndControl.ProjectAuditAndControlApi
import io.cloudflight.jems.api.project.dto.auditAndControl.AuditControlDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.ProjectAuditControlUpdateDTO
import io.cloudflight.jems.server.project.service.auditAndControl.createProjectAudit.CreateProjectAuditControlInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.getProjectAuditDetails.GetProjectAuditControlDetailsInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.listProjectAudits.ListProjectAuditsIntetractor
import io.cloudflight.jems.server.project.service.auditAndControl.updateProjectAudit.UpdateProjectAuditInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectAuditController(
    private val createAuditInteractor: CreateProjectAuditControlInteractor,
    private val updateProjectAuditInteractor: UpdateProjectAuditInteractor,
    private val listProjectAuditsIntetractor: ListProjectAuditsIntetractor,
    private val getAuditDetailsInteractor: GetProjectAuditControlDetailsInteractor,
): ProjectAuditAndControlApi {


    override fun createProjectAudit(projectId: Long, auditData: ProjectAuditControlUpdateDTO): AuditControlDTO {
       return  createAuditInteractor.createAudit(projectId, auditData.toModel()).toDto()
    }

    override fun updateProjectAudit(
        projectId: Long,
        auditControlId: Long,
        auditData: ProjectAuditControlUpdateDTO
    ): AuditControlDTO {
       return updateProjectAuditInteractor.updateAudit(
            projectId = projectId,
            auditControlId = auditControlId,
            auditControlData = auditData.toModel()
        ).toDto()
    }

    override fun listAuditsForProject(projectId: Long): List<AuditControlDTO> =
        listProjectAuditsIntetractor.listForProject(projectId).map { it.toDto() }

    override fun getAuditDetail(projectId: Long, auditControlId: Long): AuditControlDTO =
        getAuditDetailsInteractor.getDetails(projectId = projectId, auditId = auditControlId).toDto()
}