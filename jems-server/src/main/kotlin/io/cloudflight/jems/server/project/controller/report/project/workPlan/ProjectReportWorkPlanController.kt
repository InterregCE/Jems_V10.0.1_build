package io.cloudflight.jems.server.project.controller.report.project.workPlan

import io.cloudflight.jems.api.project.dto.report.project.workPlan.ProjectReportWorkPackageDTO
import io.cloudflight.jems.api.project.dto.report.project.workPlan.UpdateProjectReportWorkPackageDTO
import io.cloudflight.jems.api.project.report.project.ProjectReportWorkPlanApi
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.controller.report.partner.toProjectFile
import io.cloudflight.jems.server.project.service.report.project.workPlan.getProjectReportWorkPlan.GetProjectReportWorkPlanInteractor
import io.cloudflight.jems.server.project.service.report.project.workPlan.updateProjectReportWorkPlan.UpdateProjectReportWorkPlanInteractor
import io.cloudflight.jems.server.project.service.report.project.workPlan.uploadFileToProjectReportWorkPlan.UploadFileToProjectReportWorkPlanInteractor
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ProjectReportWorkPlanController(
    private val getReportWorkPlan: GetProjectReportWorkPlanInteractor,
    private val updateReportWorkPlan: UpdateProjectReportWorkPlanInteractor,
    private val uploadFileToReportWorkPlan: UploadFileToProjectReportWorkPlanInteractor,
) : ProjectReportWorkPlanApi {

    override fun getWorkPlan(projectId: Long, reportId: Long): List<ProjectReportWorkPackageDTO> =
        getReportWorkPlan.get(projectId = projectId, reportId = reportId).toDto()

    override fun updateWorkPlan(
        projectId: Long,
        reportId: Long,
        workPackages: List<UpdateProjectReportWorkPackageDTO>,
    ): List<ProjectReportWorkPackageDTO> =
        updateReportWorkPlan.update(
            projectId = projectId,
            reportId = reportId,
            workPlan = workPackages.toModel(),
        ).toDto()

    override fun uploadFileToActivity(projectId: Long, reportId: Long, workPackageId: Long, activityId: Long, file: MultipartFile) =
        uploadFileToReportWorkPlan.uploadToActivity(projectId, reportId, workPackageId, activityId, file.toProjectFile())
            .toDto()

    override fun uploadFileToDeliverable(projectId: Long, reportId: Long, workPackageId: Long, activityId: Long, deliverableId: Long, file: MultipartFile) =
        uploadFileToReportWorkPlan.uploadToDeliverable(projectId, reportId, workPackageId, activityId, deliverableId, file.toProjectFile())
            .toDto()

    override fun uploadFileToOutput(projectId: Long, reportId: Long, workPackageId: Long, outputId: Long, file: MultipartFile) =
        uploadFileToReportWorkPlan.uploadToOutput(projectId, reportId, workPackageId, outputId, file.toProjectFile())
            .toDto()

}
