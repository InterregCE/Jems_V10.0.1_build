package io.cloudflight.jems.api.project.workpackage;

import io.cloudflight.jems.api.project.dto.workpackage.investment.WorkPackageInvestmentDTO
import io.cloudflight.jems.api.project.dto.workpackage.investment.InvestmentSummaryDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import javax.validation.Valid
import org.springframework.web.bind.annotation.RequestParam

@Api("WorkPackageInvestment")
@RequestMapping("/api/project/{projectId}/workPackage/{workPackageId}/investment")
interface ProjectWorkPackageInvestmentApi {

    @ApiOperation("Returns investment for the work package")
    @GetMapping("/{investmentId}")
    fun getWorkPackageInvestment(
        @PathVariable investmentId: Long,
        @PathVariable projectId: Long,
        @PathVariable workPackageId: Long,
        @RequestParam(required = false) version: String? = null
    ): WorkPackageInvestmentDTO

    @ApiOperation("Returns one page of investments for the work package")
    @GetMapping
    fun getWorkPackageInvestments(
        @PathVariable projectId: Long,
        @PathVariable workPackageId: Long,
        @RequestParam(required = false) version: String? = null
    ): List<WorkPackageInvestmentDTO>

    @GetMapping("/summaries")
    fun getProjectInvestmentSummaries(
        @PathVariable projectId: Long,
        @PathVariable workPackageId: Long,
        @RequestParam(required = false) version: String? = null
    ): List<InvestmentSummaryDTO>

    @ApiOperation("Adds Investment to the work package")
    @PostMapping( consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addWorkPackageInvestment(
        @PathVariable projectId: Long,
        @PathVariable workPackageId: Long,
        @Valid @RequestBody workPackageInvestmentDTO: WorkPackageInvestmentDTO
    ): Long

    @ApiOperation("Update Investment of the work package")
    @PutMapping( consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateWorkPackageInvestment(
        @PathVariable projectId: Long,
        @PathVariable workPackageId: Long,
        @Valid @RequestBody workPackageInvestmentDTO: WorkPackageInvestmentDTO
    )

    @ApiOperation("Delete Investment of the work package")
    @DeleteMapping("/{investmentId}")
    fun deleteWorkPackageInvestment(
        @PathVariable investmentId: Long,
        @PathVariable projectId: Long,
        @PathVariable workPackageId: Long
    )

}
