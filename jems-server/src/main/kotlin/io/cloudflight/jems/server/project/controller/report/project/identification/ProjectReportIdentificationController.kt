package io.cloudflight.jems.server.project.controller.report.project.identification

import io.cloudflight.jems.api.project.dto.report.project.identification.ProjectReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.project.identification.UpdateProjectReportIdentificationDTO
import io.cloudflight.jems.api.project.report.project.ProjectReportIdentificationApi
import io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportIdentification.GetProjectReportIdentificationInteractor
import io.cloudflight.jems.server.project.service.report.project.identification.updateProjectReportIdentification.UpdateProjectReportIdentificationInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectReportIdentificationController(
    private val getProjectReportIdentification: GetProjectReportIdentificationInteractor,
    private val updateProjectReportIdentification: UpdateProjectReportIdentificationInteractor
): ProjectReportIdentificationApi {

    override fun getProjectReportIdentification(projectId: Long, reportId: Long): ProjectReportIdentificationDTO {
        return getProjectReportIdentification.getIdentification(projectId, reportId).toDto()
    }

    override fun updateProjectReportIdentification(
        projectId: Long,
        reportId: Long,
        identification: UpdateProjectReportIdentificationDTO
    ): ProjectReportIdentificationDTO {
        return updateProjectReportIdentification.updateIdentification(projectId, reportId, identification.toModel()).toDto()
    }
}
