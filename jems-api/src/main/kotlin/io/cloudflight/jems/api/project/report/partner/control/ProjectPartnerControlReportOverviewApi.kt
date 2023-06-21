package io.cloudflight.jems.api.project.report.partner.control

import io.cloudflight.jems.api.project.dto.report.partner.control.overview.ControlDeductionOverviewDTO
import io.cloudflight.jems.api.project.dto.report.partner.control.overview.ControlOverviewDTO
import io.cloudflight.jems.api.project.dto.report.partner.control.overview.ControlWorkOverviewDTO
import io.cloudflight.jems.api.project.report.partner.ProjectPartnerReportApi.Companion.ENDPOINT_API_PROJECT_PARTNER_CONTROL_REPORT
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Api("Project Partner Report Control Overview")
interface ProjectPartnerControlReportOverviewApi {

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

    @ApiOperation("Returns Partner Report Control deduction by type of errors overview")
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER_REPORT_CONTROL_OVERVIEW/deductionByTypologyOfErrors")
    fun getControlDeductionByTypologyOfErrorsOverview(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @RequestParam(required = false) linkedFormVersion: String? = null
    ): ControlDeductionOverviewDTO

    @ApiOperation("Returns Partner Report Control Overview")
    @GetMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_CONTROL_OVERVIEW)
    fun getControlOverview(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): ControlOverviewDTO

    @ApiOperation("Update Partner Report Control Overview")
    @PutMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_CONTROL_OVERVIEW, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateControlOverview(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @RequestBody controlOverview: ControlOverviewDTO,
    ): ControlOverviewDTO
}
