package io.cloudflight.jems.api.project.workpackage;

import io.cloudflight.jems.api.project.dto.workpackage.investment.WorkPackageInvestmentDTO
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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.*
import javax.validation.Valid

@Api("WorkPackageInvestment")
@RequestMapping("/api/project/workPackage/investment")
interface ProjectWorkPackageInvestmentApi {

    @ApiOperation("Returns investment for the work package")
    @GetMapping("/{investmentId}")
    fun getWorkPackageInvestment(@PathVariable investmentId: Long): WorkPackageInvestmentDTO

    @ApiOperation("Returns one page of investments for the work package")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("/forWorkPackage/{workPackageId}")
    fun getWorkPackageInvestments(@PathVariable workPackageId: Long, pageable: Pageable): Page<WorkPackageInvestmentDTO>

    @ApiOperation("Adds Investment to the work package")
    @PostMapping("/forWorkPackage/{workPackageId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addWorkPackageInvestment(
        @PathVariable workPackageId: Long,
        @Valid @RequestBody workPackageInvestmentDTO: WorkPackageInvestmentDTO
    ): Long

    @ApiOperation("Update Investment of the work package")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateWorkPackageInvestment(
        @Valid @RequestBody workPackageInvestmentDTO: WorkPackageInvestmentDTO
    )

    @ApiOperation("Delete Investment of the work package")
    @DeleteMapping("/{investmentId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteWorkPackageInvestment(
        @PathVariable investmentId: Long,
    )

}
