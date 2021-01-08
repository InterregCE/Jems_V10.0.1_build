package io.cloudflight.jems.api.project.partner

import io.cloudflight.jems.api.project.dto.partner.budget.BudgetCostsDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetGeneralCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetStaffCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetTravelAndAccommodationCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.ProjectPartnerBudgetOptionsDto
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionInputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionOutputDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.math.BigDecimal

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

    @ApiOperation("Get project partner budget costs")
    @GetMapping("/costs")
    fun getBudgetCosts(
        @PathVariable partnerId: Long
    ): BudgetCostsDTO


    @ApiOperation("Update project partner Budget: Staff Costs")
    @PutMapping("/staffcosts", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetStaffCosts(
        @PathVariable partnerId: Long,
        @RequestBody budgetStaffCostEntryDTOList: List<BudgetStaffCostEntryDTO>
    ): List<BudgetStaffCostEntryDTO>


    @ApiOperation("Update project partner Budget: Travel")
    @PutMapping("/travel", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetTravel(
        @PathVariable partnerId: Long,
        @RequestBody travelAndAccommodationCosts: List<BudgetTravelAndAccommodationCostEntryDTO>
    ): List<BudgetTravelAndAccommodationCostEntryDTO>


    @ApiOperation("Update project partner Budget: External")
    @PutMapping("/external", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetExternal(
        @PathVariable partnerId: Long,
        @RequestBody externals: List<BudgetGeneralCostEntryDTO>
    ): List<BudgetGeneralCostEntryDTO>


    @ApiOperation("Update project partner Budget: Equipment")
    @PutMapping("/equipment", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetEquipment(
        @PathVariable partnerId: Long,
        @RequestBody equipment: List<BudgetGeneralCostEntryDTO>
    ): List<BudgetGeneralCostEntryDTO>


    @ApiOperation("Update project partner Budget: Infrastructure")
    @PutMapping("/infrastructure", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetInfrastructure(
        @PathVariable partnerId: Long,
        @RequestBody infrastructures: List<BudgetGeneralCostEntryDTO>
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
