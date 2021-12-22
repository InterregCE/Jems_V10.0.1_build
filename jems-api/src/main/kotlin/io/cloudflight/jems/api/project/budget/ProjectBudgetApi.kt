package io.cloudflight.jems.api.project.budget

import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerBudgetPerFundDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerBudgetPerPeriodDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectUnitCostDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Api("Project Budget")
@RequestMapping("/api/project/{projectId}/budget/")
interface ProjectBudgetApi {
    @ApiOperation("Get project partner budget per period")
    @GetMapping("/perPeriod")
    fun getProjectPartnerBudgetPerPeriod(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): List<ProjectPartnerBudgetPerPeriodDTO>

    @ApiOperation("Get project unit costs")
    @GetMapping("unitCosts")
    fun getProjectUnitCosts(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): List<ProjectUnitCostDTO>

    @ApiOperation("Get project partner budget per fund")
    @GetMapping("/perFund")
    fun getProjectPartnerBudgetPerFund(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): List<ProjectPartnerBudgetPerFundDTO>
}
