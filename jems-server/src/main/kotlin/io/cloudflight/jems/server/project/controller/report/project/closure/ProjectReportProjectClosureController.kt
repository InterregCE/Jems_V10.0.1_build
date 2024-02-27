package io.cloudflight.jems.server.project.controller.report.project.closure

import io.cloudflight.jems.api.project.dto.report.project.projectClosure.ProjectReportProjectClosureDTO
import io.cloudflight.jems.api.project.report.project.ProjectReportProjectClosureApi
import io.cloudflight.jems.server.project.service.report.project.closure.getProjectClosure.GetProjectReportProjectClosureInteractor
import io.cloudflight.jems.server.project.service.report.project.closure.updateProjectClosure.UpdateProjectReportProjectClosureInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectReportProjectClosureController(
    private val getProjectReportProjectClosure: GetProjectReportProjectClosureInteractor,
    private val updateProjectReportProjectClosure: UpdateProjectReportProjectClosureInteractor
) : ProjectReportProjectClosureApi {

    override fun getProjectClosure(projectId: Long, reportId: Long): ProjectReportProjectClosureDTO =
        getProjectReportProjectClosure.get(projectId, reportId).toDto()

    override fun updateProjectClosure(
        projectId: Long,
        reportId: Long,
        projectClosure: ProjectReportProjectClosureDTO
    ): ProjectReportProjectClosureDTO =
        updateProjectReportProjectClosure.update(reportId, projectClosure.toModel()).toDto()

}
