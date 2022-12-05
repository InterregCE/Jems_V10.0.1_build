package io.cloudflight.jems.api.project.report.control

import io.cloudflight.jems.api.project.dto.report.partner.control.overview.ControlWorkOverviewDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportApi.Companion.ENDPOINT_API_PROJECT_PARTNER_CONTROL_REPORT
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Api("Project Partner Report Control Overview")
interface ProjectPartnerReportControlOverviewApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_PARTNER_REPORT_CONTROL_OVERVIEW =
            "$ENDPOINT_API_PROJECT_PARTNER_CONTROL_REPORT/controlOverview/byPartnerId/{partnerId}/byReportId/{reportId}"
    }

    @ApiOperation("Returns Partner Report Control work overview")
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER_REPORT_CONTROL_OVERVIEW/work")
    fun getControlWorkOverview(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): ControlWorkOverviewDTO

}
