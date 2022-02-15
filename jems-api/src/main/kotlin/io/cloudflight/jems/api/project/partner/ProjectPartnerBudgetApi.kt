package io.cloudflight.jems.api.project.partner

import io.cloudflight.jems.api.project.dto.partner.budget.BudgetCostsDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetGeneralCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetSpfCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetStaffCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetTravelAndAccommodationCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetUnitCostEntryDTO
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
import org.springframework.web.bind.annotation.RequestParam
import java.math.BigDecimal

@Api("Project Partner Budget")
interface ProjectPartnerBudgetApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_PARTNER_BUDGET = "/api/project/partner/{partnerId}/budget"
    }

    @ApiOperation("Get project partner Budget Options")
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER_BUDGET/options")
    fun getBudgetOptions(
        @PathVariable partnerId: Long,
        @RequestParam(required = false) version: String? = null
    ): ProjectPartnerBudgetOptionsDto?

    @ApiOperation("Update project partner Budget Options")
    @PutMapping("$ENDPOINT_API_PROJECT_PARTNER_BUDGET/options", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetOptions(
        @PathVariable partnerId: Long,
        @RequestBody budgetOptionsDto: ProjectPartnerBudgetOptionsDto
    )

    @ApiOperation("Get project partner budget costs")
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER_BUDGET/costs")
    fun getBudgetCosts(
        @PathVariable partnerId: Long,
        @RequestParam(required = false) version: String? = null
    ): BudgetCostsDTO


    @ApiOperation("Update project partner Budget: Staff Costs")
    @PutMapping("$ENDPOINT_API_PROJECT_PARTNER_BUDGET/staffcosts", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetStaffCosts(
        @PathVariable partnerId: Long,
        @RequestBody budgetStaffCostEntryDTOList: List<BudgetStaffCostEntryDTO>
    ): List<BudgetStaffCostEntryDTO>


    @ApiOperation("Update project partner Budget: Travel")
    @PutMapping("$ENDPOINT_API_PROJECT_PARTNER_BUDGET/travel", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetTravel(
        @PathVariable partnerId: Long,
        @RequestBody travelAndAccommodationCosts: List<BudgetTravelAndAccommodationCostEntryDTO>
    ): List<BudgetTravelAndAccommodationCostEntryDTO>


    @ApiOperation("Update project partner Budget: External")
    @PutMapping("$ENDPOINT_API_PROJECT_PARTNER_BUDGET/external", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetExternal(
        @PathVariable partnerId: Long,
        @RequestBody externals: List<BudgetGeneralCostEntryDTO>
    ): List<BudgetGeneralCostEntryDTO>


    @ApiOperation("Update project partner Budget: Equipment")
    @PutMapping("$ENDPOINT_API_PROJECT_PARTNER_BUDGET/equipment", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetEquipment(
        @PathVariable partnerId: Long,
        @RequestBody equipment: List<BudgetGeneralCostEntryDTO>
    ): List<BudgetGeneralCostEntryDTO>


    @ApiOperation("Update project partner Budget: Infrastructure")
    @PutMapping("$ENDPOINT_API_PROJECT_PARTNER_BUDGET/infrastructure", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetInfrastructure(
        @PathVariable partnerId: Long,
        @RequestBody infrastructures: List<BudgetGeneralCostEntryDTO>
    ): List<BudgetGeneralCostEntryDTO>


    @ApiOperation("Update project partner Budget: Unit Costs")
    @PutMapping("$ENDPOINT_API_PROJECT_PARTNER_BUDGET/unitcosts", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetUnitCosts(
        @PathVariable partnerId: Long,
        @RequestBody unitCosts: List<BudgetUnitCostEntryDTO>
    ): List<BudgetUnitCostEntryDTO>


    @ApiOperation("Update project partner Budget: SPF Costs")
    @PutMapping("$ENDPOINT_API_PROJECT_PARTNER_BUDGET/spf", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBudgetSpfCosts(
        @PathVariable partnerId: Long,
        @RequestBody spfCosts: List<BudgetSpfCostEntryDTO>
    ): List<BudgetSpfCostEntryDTO>


    @ApiOperation("Get project partner Budget: total")
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER_BUDGET/total")
    fun getTotal(
        @PathVariable partnerId: Long,
        @RequestParam(required = false) version: String? = null
    ): BigDecimal

    @ApiOperation("Get project partner Co-Financing")
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER_BUDGET/cofinancing")
    fun getProjectPartnerCoFinancing(
        @PathVariable partnerId: Long,
        @RequestParam(required = false) version: String? = null
    ): ProjectPartnerCoFinancingAndContributionOutputDTO

    @ApiOperation("Update project partner co-financing")
    @PutMapping("$ENDPOINT_API_PROJECT_PARTNER_BUDGET/cofinancing", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectPartnerCoFinancing(
        @PathVariable partnerId: Long,
        @RequestBody partnerCoFinancing: ProjectPartnerCoFinancingAndContributionInputDTO
    ): ProjectPartnerCoFinancingAndContributionOutputDTO

}
