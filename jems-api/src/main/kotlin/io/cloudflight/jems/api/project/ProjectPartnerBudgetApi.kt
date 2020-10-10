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

    //region StuffCosts

    @ApiOperation("Get project partner Budget: Staff Costs")
    @GetMapping("/staffcost")
    fun getBudgetStaffCost(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long
    ): List<InputBudget>

    @ApiOperation("Update project partner Budget: Staff Costs")
    @PutMapping("/staffcost", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetStaffCost(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long,
        @Valid @RequestBody budgetCosts: List<InputBudget>
    ): List<InputBudget>
    //endregion StuffCosts

    //region Travel

    @ApiOperation("Get project partner Budget: Travel")
    @GetMapping("/travel")
    fun getBudgetTravel(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long
    ): List<InputBudget>

    @ApiOperation("Update project partner Budget: Travel")
    @PutMapping("/travel", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetTravel(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long,
        @Valid @RequestBody travels: List<InputBudget>
    ): List<InputBudget>
    //endregion Travel

    //region External

    @ApiOperation("Get project partner Budget: External")
    @GetMapping("/external")
    fun getBudgetExternal(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long
    ): List<InputBudget>

    @ApiOperation("Update project partner Budget: External")
    @PutMapping("/external", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetExternal(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long,
        @Valid @RequestBody externals: List<InputBudget>
    ): List<InputBudget>
    //endregion External

    //region Equipment

    @ApiOperation("Get project partner Budget: Equipment")
    @GetMapping("/equipment")
    fun getBudgetEquipment(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long
    ): List<InputBudget>

    @ApiOperation("Update project partner Budget: Equipment")
    @PutMapping("/equipment", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetEquipment(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long,
        @Valid @RequestBody equipments: List<InputBudget>
    ): List<InputBudget>
    //endregion Equipment

    //region Infrastructure

    @ApiOperation("Get project partner Budget: Infrastructure")
    @GetMapping("/infrastructure")
    fun getBudgetInfrastructure(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long
    ): List<InputBudget>

    @ApiOperation("Update project partner Budget: Infrastructure")
    @PutMapping("/infrastructure", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetInfrastructure(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long,
        @Valid @RequestBody infrastructures: List<InputBudget>
    ): List<InputBudget>
    //endregion Infrastructure

}
