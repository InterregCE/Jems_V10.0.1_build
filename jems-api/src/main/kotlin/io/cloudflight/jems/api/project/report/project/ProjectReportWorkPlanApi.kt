package io.cloudflight.jems.api.project.report.project

import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.project.dto.report.project.workPlan.ProjectReportWorkPackageDTO
import io.cloudflight.jems.api.project.dto.report.project.workPlan.UpdateProjectReportWorkPackageDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Api("Project Report Work Plan")
interface ProjectReportWorkPlanApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_REPORT_WORK_PLAN =
            "${ProjectReportApi.ENDPOINT_API_PROJECT_REPORT}/workPlan"
    }

    @ApiOperation("Returns all project report work packages")
    @GetMapping(ENDPOINT_API_PROJECT_REPORT_WORK_PLAN)
    fun getWorkPlan(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
    ): List<ProjectReportWorkPackageDTO>

    @ApiOperation("Updates project report work packages")
    @PutMapping(ENDPOINT_API_PROJECT_REPORT_WORK_PLAN, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateWorkPlan(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @RequestBody workPackages: List<UpdateProjectReportWorkPackageDTO>
    ): List<ProjectReportWorkPackageDTO>

    @ApiOperation("Upload file to activity")
    @PostMapping(
        "$ENDPOINT_API_PROJECT_REPORT_WORK_PLAN/byWpId/{workPackageId}/byActivityId/{activityId}/file",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadFileToActivity(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable workPackageId: Long,
        @PathVariable activityId: Long,
        @RequestPart("file") file: MultipartFile
    ): JemsFileMetadataDTO

    @ApiOperation("Upload file to deliverable")
    @PostMapping(
        "$ENDPOINT_API_PROJECT_REPORT_WORK_PLAN/byWpId/{workPackageId}/byActivityId/{activityId}/byDeliverableId/{deliverableId}/file",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadFileToDeliverable(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable workPackageId: Long,
        @PathVariable activityId: Long,
        @PathVariable deliverableId: Long,
        @RequestPart("file") file: MultipartFile
    ): JemsFileMetadataDTO

    @ApiOperation("Upload file to output")
    @PostMapping(
        "$ENDPOINT_API_PROJECT_REPORT_WORK_PLAN/byWpId/{workPackageId}/byOutputId/{outputId}/file",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadFileToOutput(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable workPackageId: Long,
        @PathVariable outputId: Long,
        @RequestPart("file") file: MultipartFile
    ): JemsFileMetadataDTO

}
