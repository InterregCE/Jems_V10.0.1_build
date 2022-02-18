package io.cloudflight.jems.api.project.report

import io.cloudflight.jems.api.project.dto.report.partner.workPlan.ProjectPartnerReportWorkPackageDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.UpdateProjectPartnerReportWorkPackageDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportApi.Companion.ENDPOINT_API_PROJECT_PARTNER_REPORT
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

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
    @PutMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_WORK_PLAN)
    fun updateWorkPlan(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @RequestBody workPackages: List<UpdateProjectPartnerReportWorkPackageDTO>
    ): List<ProjectPartnerReportWorkPackageDTO>

}
