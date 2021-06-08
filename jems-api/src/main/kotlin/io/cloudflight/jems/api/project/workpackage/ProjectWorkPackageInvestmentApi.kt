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
@RequestMapping("/api/project/workPackage/investment")
interface ProjectWorkPackageInvestmentApi {

    @ApiOperation("Returns investment for the work package")
    @GetMapping("/{investmentId}")
    fun getWorkPackageInvestment(@PathVariable investmentId: Long, @RequestParam(required = false) version: String? = null): WorkPackageInvestmentDTO

    @ApiOperation("Returns one page of investments for the work package")
    @GetMapping("/forWorkPackage/{workPackageId}")
    fun getWorkPackageInvestments(@PathVariable workPackageId: Long, @RequestParam(required = false) version: String? = null): List<WorkPackageInvestmentDTO>

    @GetMapping("/forProject/{projectId}")
    fun getProjectInvestmentSummaries(@PathVariable projectId: Long): List<InvestmentSummaryDTO>

    @ApiOperation("Adds Investment to the work package")
    @PostMapping("/forWorkPackage/{workPackageId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addWorkPackageInvestment(
        @PathVariable workPackageId: Long,
        @Valid @RequestBody workPackageInvestmentDTO: WorkPackageInvestmentDTO
    ): Long

    @ApiOperation("Update Investment of the work package")
    @PutMapping("/forWorkPackage/{workPackageId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateWorkPackageInvestment(
        @PathVariable workPackageId: Long,
        @Valid @RequestBody workPackageInvestmentDTO: WorkPackageInvestmentDTO
    )

    @ApiOperation("Delete Investment of the work package")
    @DeleteMapping("/forWorkPackage/{workPackageId}/{investmentId}")
    fun deleteWorkPackageInvestment(
        @PathVariable workPackageId: Long,
        @PathVariable investmentId: Long
    )

}
