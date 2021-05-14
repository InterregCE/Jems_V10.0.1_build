package io.cloudflight.jems.server.project.controller.partner.budget

import io.cloudflight.jems.api.project.dto.partner.budget.BudgetGeneralCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetStaffCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetTravelAndAccommodationCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetUnitCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.ProjectPartnerBudgetOptionsDto
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionInputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionOutputDTO
import io.cloudflight.jems.api.project.partner.ProjectPartnerBudgetApi
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_costs.GetBudgetCostsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_options.GetBudgetOptionsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCostInteractor
import io.cloudflight.jems.server.project.service.partner.budget.update_budge_staff_costs.UpdateBudgetStaffCostsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_unit_costs.UpdateBudgetUnitCostsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.update_budget_equipment_costs.UpdateBudgetEquipmentCosts
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.update_budget_external_expertise_and_services.UpdateBudgetExternalExpertiseAndServicesCostsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.update_budget_infrastructure_and_works_costs.UpdateBudgetInfrastructureAndWorksCostsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_options.UpdateBudgetOptionsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_travel_and_accommodation_costs.UpdateBudgetTravelAndAccommodationCostsInteractor
import io.cloudflight.jems.server.project.service.partner.cofinancing.get_cofinancing.GetCoFinancingInteractor
import io.cloudflight.jems.server.project.service.partner.cofinancing.toContributionModel
import io.cloudflight.jems.server.project.service.partner.cofinancing.toDto
import io.cloudflight.jems.server.project.service.partner.cofinancing.toFinancingModel
import io.cloudflight.jems.server.project.service.partner.cofinancing.update_cofinancing.UpdateCoFinancingInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerBudgetController(
    private val getBudgetOptions: GetBudgetOptionsInteractor,
    private val updateBudgetOptions: UpdateBudgetOptionsInteractor,
    private val getCoFinancing: GetCoFinancingInteractor,
    private val getBudgetCosts: GetBudgetCostsInteractor,
    private val updateCoFinancing: UpdateCoFinancingInteractor,
    private val updateBudgetEquipmentCosts: UpdateBudgetEquipmentCosts,
    private val updateBudgetTravelAndAccommodationCosts: UpdateBudgetTravelAndAccommodationCostsInteractor,
    private val updateBudgetExternalExpertiseAndServicesCosts: UpdateBudgetExternalExpertiseAndServicesCostsInteractor,
    private val updateBudgetInfrastructureAndWorksCosts: UpdateBudgetInfrastructureAndWorksCostsInteractor,
    private val updateBudgetStaffCosts: UpdateBudgetStaffCostsInteractor,
    private val updateBudgetUnitCosts: UpdateBudgetUnitCostsInteractor,
    private val getBudgetTotalCost: GetBudgetTotalCostInteractor
) : ProjectPartnerBudgetApi {

    override fun updateBudgetStaffCosts(partnerId: Long, budgetStaffCostEntryDTOList: List<BudgetStaffCostEntryDTO>) =
        updateBudgetStaffCosts.updateBudgetStaffCosts(partnerId, budgetStaffCostEntryDTOList.toBudgetStaffCostEntryList()).toBudgetStaffCostEntryDTOList()

    override fun getBudgetOptions(partnerId: Long): ProjectPartnerBudgetOptionsDto =
        getBudgetOptions.getBudgetOptions(partnerId)?.toProjectPartnerBudgetOptionsDto()
            ?: ProjectPartnerBudgetOptionsDto()

    override fun updateBudgetOptions(partnerId: Long, budgetOptionsDto: ProjectPartnerBudgetOptionsDto) =
        updateBudgetOptions.updateBudgetOptions(partnerId, budgetOptionsDto.toProjectPartnerBudgetOptions(partnerId))

    override fun getBudgetCosts(partnerId: Long, version: Int?) =
        getBudgetCosts.getBudgetCosts(partnerId, version).toBudgetCostsDTO()

    override fun updateBudgetTravel(partnerId: Long, travelAndAccommodationCosts: List<BudgetTravelAndAccommodationCostEntryDTO>) =
        updateBudgetTravelAndAccommodationCosts.updateBudgetTravelAndAccommodationCosts(partnerId, travelAndAccommodationCosts.toBudgetTravelAndAccommodationCostEntryList()).toBudgetTravelAndAccommodationCostsEntryDTOList()

    override fun updateBudgetExternal(partnerId: Long, externals: List<BudgetGeneralCostEntryDTO>) =
        updateBudgetExternalExpertiseAndServicesCosts.updateBudgetGeneralCosts(partnerId, externals.toBudgetGeneralCostEntryList()).toBudgetGeneralCostsEntryDTOList()

    override fun updateBudgetEquipment(partnerId: Long, equipment: List<BudgetGeneralCostEntryDTO>) =
        updateBudgetEquipmentCosts.updateBudgetGeneralCosts(partnerId, equipment.toBudgetGeneralCostEntryList()).toBudgetGeneralCostsEntryDTOList()

    override fun updateBudgetInfrastructure(partnerId: Long, infrastructures: List<BudgetGeneralCostEntryDTO>) =
        updateBudgetInfrastructureAndWorksCosts.updateBudgetGeneralCosts(partnerId, infrastructures.toBudgetGeneralCostEntryList()).toBudgetGeneralCostsEntryDTOList()

    override fun updateBudgetUnitCosts(partnerId: Long, unitCosts: List<BudgetUnitCostEntryDTO>) =
        updateBudgetUnitCosts.updateBudgetUnitCosts(partnerId, unitCosts.toBudgetUnitCostEntryList()).toBudgetUnitCostEntryDTOList()

    override fun getTotal(partnerId: Long, version: Int?) =
        getBudgetTotalCost.getBudgetTotalCost(partnerId, version)

    override fun getProjectPartnerCoFinancing(partnerId: Long): ProjectPartnerCoFinancingAndContributionOutputDTO =
        getCoFinancing.getCoFinancing(partnerId).toDto()

    override fun updateProjectPartnerCoFinancing(partnerId: Long, partnerCoFinancing: ProjectPartnerCoFinancingAndContributionInputDTO) =
        updateCoFinancing.updateCoFinancing(
            partnerId,
            partnerCoFinancing.finances.toFinancingModel(),
            partnerCoFinancing.partnerContributions.toContributionModel()
        ).toDto()

}
