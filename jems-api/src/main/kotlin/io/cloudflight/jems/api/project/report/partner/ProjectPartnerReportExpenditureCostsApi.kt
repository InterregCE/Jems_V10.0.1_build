package io.cloudflight.jems.api.project.report.partner

import io.cloudflight.jems.api.project.dto.partner.budget.ProjectPartnerBudgetOptionsDto
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportExpenditureCostDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportInvestmentDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportLumpSumDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportUnitCostDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Api("Project Partner Report Expenditure costs")
interface ProjectPartnerReportExpenditureCostsApi {

    companion object {
        const val ENDPOINT_API_PARTNER_REPORT_EXPENDITURE_COSTS =
            "${ProjectPartnerReportApi.ENDPOINT_API_PROJECT_PARTNER_REPORT}/expenditure/byPartnerId/{partnerId}/byReportId/{reportId}"
    }

    @ApiOperation("Returns all expenditure costs by partner id and report id")
    @GetMapping(ENDPOINT_API_PARTNER_REPORT_EXPENDITURE_COSTS)
    fun getProjectPartnerReports(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): List<ProjectPartnerReportExpenditureCostDTO>

    @ApiOperation("Update partner report expenditure costs")
    @PutMapping(ENDPOINT_API_PARTNER_REPORT_EXPENDITURE_COSTS, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updatePartnerReportExpenditures(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @RequestBody expenditureCosts: List<ProjectPartnerReportExpenditureCostDTO>
    ): List<ProjectPartnerReportExpenditureCostDTO>

    @ApiOperation("Upload file to expenditures")
    @PostMapping("$ENDPOINT_API_PARTNER_REPORT_EXPENDITURE_COSTS/byExpenditureId/{expenditureId}/file", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadFileToExpenditure(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @PathVariable expenditureId: Long,
        @RequestPart("file") file: MultipartFile,
    ): ProjectReportFileMetadataDTO

    @ApiOperation("Returns all Lump Sums available for this report")
    @GetMapping("$ENDPOINT_API_PARTNER_REPORT_EXPENDITURE_COSTS/lumpSums")
    fun getAvailableLumpSums(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): List<ProjectPartnerReportLumpSumDTO>

    @ApiOperation("Returns all Unit Costs available for this report")
    @GetMapping("$ENDPOINT_API_PARTNER_REPORT_EXPENDITURE_COSTS/unitCosts")
    fun getAvailableUnitCosts(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): List<ProjectPartnerReportUnitCostDTO>

    @ApiOperation("Returns all Investments available for this report")
    @GetMapping("$ENDPOINT_API_PARTNER_REPORT_EXPENDITURE_COSTS/investments")
    fun getAvailableInvestments(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): List<ProjectPartnerReportInvestmentDTO>

    @ApiOperation("Returns all budget options available for this report")
    @GetMapping("$ENDPOINT_API_PARTNER_REPORT_EXPENDITURE_COSTS/budgetOptions")
    fun getAvailableBudgetOptions(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
    ): ProjectPartnerBudgetOptionsDto

    @ApiOperation("Returns all available parked expenditures for re-injection")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("${ProjectPartnerReportApi.ENDPOINT_API_PROJECT_PARTNER_REPORT}/expenditure/byPartnerId/{partnerId}/parked")
    fun getAvailableParkedExpenditures(
        @PathVariable partnerId: Long,
        pageable: Pageable,
    ): Page<ProjectPartnerReportExpenditureCostDTO>

    @ApiOperation("Update partner report expenditure costs")
    @PutMapping("$ENDPOINT_API_PARTNER_REPORT_EXPENDITURE_COSTS/reIncludeExpenditure/{expenditureId}")
    fun reIncludeParkedExpenditure(
        @PathVariable partnerId: Long,
        @PathVariable reportId: Long,
        @PathVariable expenditureId: Long,
    )

}
