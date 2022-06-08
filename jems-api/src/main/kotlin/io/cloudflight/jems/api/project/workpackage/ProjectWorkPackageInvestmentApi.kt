package io.cloudflight.jems.api.project.workpackage;

import io.cloudflight.jems.api.project.dto.workpackage.investment.WorkPackageInvestmentDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Api("WorkPackageInvestment")
interface ProjectWorkPackageInvestmentApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_WORK_PACKAGE_INVESTMENT =
            "/api/project/{projectId}/workPackage/{workPackageId}/investment"
    }

    @ApiOperation("Returns investment for the work package")
    @GetMapping("$ENDPOINT_API_PROJECT_WORK_PACKAGE_INVESTMENT/{investmentId}")
    fun getWorkPackageInvestment(
        @PathVariable investmentId: Long,
        @PathVariable projectId: Long,
        @PathVariable workPackageId: Long,
        @RequestParam(required = false) version: String? = null
    ): WorkPackageInvestmentDTO

    @ApiOperation("Returns one page of investments for the work package")
    @GetMapping(ENDPOINT_API_PROJECT_WORK_PACKAGE_INVESTMENT)
    fun getWorkPackageInvestments(
        @PathVariable projectId: Long,
        @PathVariable workPackageId: Long,
        @RequestParam(required = false) version: String? = null
    ): List<WorkPackageInvestmentDTO>

    @ApiOperation("Adds Investment to the work package")
    @PostMapping(ENDPOINT_API_PROJECT_WORK_PACKAGE_INVESTMENT, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addWorkPackageInvestment(
        @PathVariable projectId: Long,
        @PathVariable workPackageId: Long,
        @RequestBody workPackageInvestmentDTO: WorkPackageInvestmentDTO
    ): Long

    @ApiOperation("Update Investment of the work package")
    @PutMapping(ENDPOINT_API_PROJECT_WORK_PACKAGE_INVESTMENT, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateWorkPackageInvestment(
        @PathVariable projectId: Long,
        @PathVariable workPackageId: Long,
        @RequestBody workPackageInvestmentDTO: WorkPackageInvestmentDTO
    )

    @ApiOperation("Delete Investment of the work package")
    @DeleteMapping("$ENDPOINT_API_PROJECT_WORK_PACKAGE_INVESTMENT/{investmentId}")
    fun deleteWorkPackageInvestment(
        @PathVariable investmentId: Long,
        @PathVariable projectId: Long,
        @PathVariable workPackageId: Long
    )

}
