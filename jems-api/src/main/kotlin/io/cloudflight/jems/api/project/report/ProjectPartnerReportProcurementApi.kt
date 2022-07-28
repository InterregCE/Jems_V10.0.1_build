package io.cloudflight.jems.api.project.report

import io.cloudflight.jems.api.common.dto.IdNamePairDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.ProjectPartnerReportProcurementChangeDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.ProjectPartnerReportProcurementDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.ProjectPartnerReportProcurementSummaryDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportApi.Companion.ENDPOINT_API_PROJECT_PARTNER_REPORT
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Partner Report Procurement")
interface ProjectPartnerReportProcurementApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT =
            "$ENDPOINT_API_PROJECT_PARTNER_REPORT/procurement/byPartnerId/{partnerId}/byReportId/{reportId}"
    }

    @ApiOperation("Returns all project partner report procurements")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string", allowMultiple = true)
    )
    @GetMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT)
    fun getProcurement(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        pageable: Pageable,
    ): Page<ProjectPartnerReportProcurementSummaryDTO>

    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT/byProcurementId/{procurementId}")
    fun getProcurementById(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @PathVariable procurementId: Long,
    ): ProjectPartnerReportProcurementDTO

    @ApiOperation("Add new project partner report procurement")
    @PostMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addNewProcurement(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @RequestBody procurementData: ProjectPartnerReportProcurementChangeDTO,
    ): ProjectPartnerReportProcurementDTO

    @ApiOperation("Updates project partner report procurements")
    @PutMapping(ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProcurement(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @RequestBody procurementData: ProjectPartnerReportProcurementChangeDTO,
    ): ProjectPartnerReportProcurementDTO

    @ApiOperation("Delete procurement from report")
    @DeleteMapping("$ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT/byProcurementId/{procurementId}")
    fun deleteProcurement(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @PathVariable procurementId: Long,
    )

    @ApiOperation("Returns all project partner report procurements")
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER_REPORT_PROCUREMENT/selector")
    fun getProcurementSelectorList(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): List<IdNamePairDTO>

}
