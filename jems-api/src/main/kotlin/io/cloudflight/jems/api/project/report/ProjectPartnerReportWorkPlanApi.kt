package io.cloudflight.jems.api.project.report

import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.ProjectPartnerReportWorkPackageDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.UpdateProjectPartnerReportWorkPackageDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportApi.Companion.ENDPOINT_API_PROJECT_PARTNER_REPORT
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

@Api("Project Partner Report Work Plan")
interface ProjectPartnerReportWorkPlanApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_PARTNER_REPORT_WORK_PLAN =
            "$ENDPOINT_API_PROJECT_PARTNER_REPORT/workPlan/byPartnerId/{partnerId}/byReportId/{reportId}"
    }

    @ApiOperation("Returns all project partner report work packages")
    @GetMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_WORK_PLAN)
    fun getWorkPlan(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): List<ProjectPartnerReportWorkPackageDTO>

    @ApiOperation("Updates project partner report work packages")
    @PutMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_WORK_PLAN, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateWorkPlan(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @RequestBody workPackages: List<UpdateProjectPartnerReportWorkPackageDTO>
    ): List<ProjectPartnerReportWorkPackageDTO>

    @ApiOperation("Upload file to activity")
    @PostMapping(
        "$ENDPOINT_API_PROJECT_PARTNER_REPORT_WORK_PLAN/byWpId/{workPackageId}/byActivityId/{activityId}/file",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadFileToActivity(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @PathVariable workPackageId: Long,
        @PathVariable activityId: Long,
        @RequestPart("file") file: MultipartFile
    ): ProjectReportFileMetadataDTO

    @ApiOperation("Upload file to deliverable")
    @PostMapping(
        "$ENDPOINT_API_PROJECT_PARTNER_REPORT_WORK_PLAN/byWpId/{workPackageId}/byActivityId/{activityId}/byDeliverableId/{deliverableId}/file",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadFileToDeliverable(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @PathVariable workPackageId: Long,
        @PathVariable activityId: Long,
        @PathVariable deliverableId: Long,
        @RequestPart("file") file: MultipartFile
    ): ProjectReportFileMetadataDTO

    @ApiOperation("Upload file to output")
    @PostMapping(
        "$ENDPOINT_API_PROJECT_PARTNER_REPORT_WORK_PLAN/byWpId/{workPackageId}/byOutputId/{outputId}/file",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadFileToOutput(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @PathVariable workPackageId: Long,
        @PathVariable outputId: Long,
        @RequestPart("file") file: MultipartFile
    ): ProjectReportFileMetadataDTO

}
