package io.cloudflight.jems.api.project

import io.cloudflight.jems.api.project.dto.partner.budget.InputBudget
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Api("Project Partner Budget")
@RequestMapping("/api/project/{projectId}/partner/{partnerId}/")
interface ProjectPartnerBudgetApi {

    @ApiOperation("Get project partner Staff Costs")
    @GetMapping("/staffcost")
    fun getBudgetStaffCost(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long
    ): List<InputBudget>

    @ApiOperation("Update project partner Staff Costs")
    @PutMapping("/staffcost", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetStaffCost(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long,
        @Valid @RequestBody budgetCosts: List<InputBudget>
    ): List<InputBudget>

}
