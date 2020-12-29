package io.cloudflight.jems.api.project.partner

import io.cloudflight.jems.api.project.dto.partner.budget.BudgetGeneralCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetStaffCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetTravelAndAccommodationCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.ProjectPartnerBudgetOptionsDto
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionOutputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionInputDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.math.BigDecimal
import javax.validation.Valid

@Api("Project Partner Budget")
@RequestMapping("/api/project/partner/{partnerId}/budget/")
interface ProjectPartnerBudgetApi {

    @ApiOperation("Get project partner Budget Options")
    @GetMapping("/options")
    fun getBudgetOptions(
        @PathVariable partnerId: Long
    ): ProjectPartnerBudgetOptionsDto?

    @ApiOperation("Update project partner Budget Options")
    @PutMapping("/options", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetOptions(
        @PathVariable partnerId: Long,
        @RequestBody budgetOptionsDto: ProjectPartnerBudgetOptionsDto
    )

    @ApiOperation("Get project partner Budget: Staff Costs")
    @GetMapping("/staffcosts")
    fun getBudgetStaffCosts(
        @PathVariable partnerId: Long
    ): List<BudgetStaffCostEntryDTO>

    @ApiOperation("Update project partner Budget: Staff Costs")
    @PutMapping("/staffcosts", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetStaffCosts(
        @PathVariable partnerId: Long,
        @Valid @RequestBody budgetStaffCostEntryDTOList: List<BudgetStaffCostEntryDTO>
    ): List<BudgetStaffCostEntryDTO>

    @ApiOperation("Get project partner Budget: Travel")
    @GetMapping("/travel")
    fun getBudgetTravel(
        @PathVariable partnerId: Long
    ): List<BudgetTravelAndAccommodationCostEntryDTO>

    @ApiOperation("Update project partner Budget: Travel")
    @PutMapping("/travel", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetTravel(
        @PathVariable partnerId: Long,
        @Valid @RequestBody travelAndAccommodationCosts: List<BudgetTravelAndAccommodationCostEntryDTO>
    ): List<BudgetTravelAndAccommodationCostEntryDTO>

    @ApiOperation("Get project partner Budget: External")
    @GetMapping("/external")
    fun getBudgetExternal(
        @PathVariable partnerId: Long
    ): List<BudgetGeneralCostEntryDTO>

    @ApiOperation("Update project partner Budget: External")
    @PutMapping("/external", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetExternal(
        @PathVariable partnerId: Long,
        @Valid @RequestBody externals: List<BudgetGeneralCostEntryDTO>
    ): List<BudgetGeneralCostEntryDTO>

    @ApiOperation("Get project partner Budget: Equipment")
    @GetMapping("/equipment")
    fun getBudgetEquipment(
        @PathVariable partnerId: Long
    ): List<BudgetGeneralCostEntryDTO>

    @ApiOperation("Update project partner Budget: Equipment")
    @PutMapping("/equipment", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetEquipment(
        @PathVariable partnerId: Long,
        @Valid @RequestBody equipment: List<BudgetGeneralCostEntryDTO>
    ): List<BudgetGeneralCostEntryDTO>

    @ApiOperation("Get project partner Budget: Infrastructure")
    @GetMapping("/infrastructure")
    fun getBudgetInfrastructure(
        @PathVariable partnerId: Long
    ): List<BudgetGeneralCostEntryDTO>

    @ApiOperation("Update project partner Budget: Infrastructure")
    @PutMapping("/infrastructure", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetInfrastructure(
        @PathVariable partnerId: Long,
        @Valid @RequestBody infrastructures: List<BudgetGeneralCostEntryDTO>
    ): List<BudgetGeneralCostEntryDTO>

    @ApiOperation("Get project partner Budget: total")
    @GetMapping("/total")
    fun getTotal(
        @PathVariable partnerId: Long
    ): BigDecimal

    @ApiOperation("Get project partner Co-Financing")
    @GetMapping("/cofinancing")
    fun getProjectPartnerCoFinancing(
        @PathVariable partnerId: Long
    ): ProjectPartnerCoFinancingAndContributionOutputDTO

    @ApiOperation("Update project partner co-financing")
    @PutMapping("/cofinancing", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectPartnerCoFinancing(
        @PathVariable partnerId: Long,
        @RequestBody partnerCoFinancing: ProjectPartnerCoFinancingAndContributionInputDTO
    ): ProjectPartnerCoFinancingAndContributionOutputDTO

}
