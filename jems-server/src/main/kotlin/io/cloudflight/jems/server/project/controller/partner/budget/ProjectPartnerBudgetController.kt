package io.cloudflight.jems.server.project.controller.partner.budget

import io.cloudflight.jems.api.project.dto.partner.budget.BudgetGeneralCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetTravelAndAccommodationCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.ProjectPartnerBudgetOptionsDto
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetStaffCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionInputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionOutputDTO
import io.cloudflight.jems.api.project.partner.ProjectPartnerBudgetApi
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_general_costs.get_budget_equipment_costs.GetBudgetEquipmentCosts
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_general_costs.get_budget_external_expertise_and_services.GetBudgetExternalExpertiseAndServicesCostsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_general_costs.get_budget_infrastructure_and_works_costs.GetBudgetInfrastructureAndWorksCostsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_options.GetBudgetOptionsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_staff_costs.GetBudgetStaffCostsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCostInteractor
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_travel_and_accommodation_costs.GetBudgetTravelAndAccommodationCostsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.update_budge_staff_costs.UpdateBudgetStaffCostsInteractor
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
    private val updateCoFinancing: UpdateCoFinancingInteractor,
    private val getBudgetEquipmentCosts: GetBudgetEquipmentCosts,
    private val getBudgetExternalExpertiseAndServicesCosts: GetBudgetExternalExpertiseAndServicesCostsInteractor,
    private val getBudgetInfrastructureAndWorksCosts: GetBudgetInfrastructureAndWorksCostsInteractor,
    private val getBudgetTravelAndAccommodationCosts: GetBudgetTravelAndAccommodationCostsInteractor,
    private val getBudgetStaffCosts: GetBudgetStaffCostsInteractor,
    private val updateBudgetEquipmentCosts: UpdateBudgetEquipmentCosts,
    private val updateBudgetTravelAndAccommodationCosts: UpdateBudgetTravelAndAccommodationCostsInteractor,
    private val updateBudgetExternalExpertiseAndServicesCosts: UpdateBudgetExternalExpertiseAndServicesCostsInteractor,
    private val updateBudgetInfrastructureAndWorksCosts: UpdateBudgetInfrastructureAndWorksCostsInteractor,
    private val updateBudgetStaffCosts: UpdateBudgetStaffCostsInteractor,
    private val getBudgetTotalCost: GetBudgetTotalCostInteractor
) : ProjectPartnerBudgetApi {

    override fun getBudgetStaffCosts(partnerId: Long) =
        getBudgetStaffCosts.getBudgetStaffCosts(partnerId).toBudgetStaffCostEntryDTOList()

    override fun updateBudgetStaffCosts(partnerId: Long, budgetStaffCostEntryDTOList: List<BudgetStaffCostEntryDTO>) =
        updateBudgetStaffCosts.updateBudgetStaffCosts(partnerId, budgetStaffCostEntryDTOList.toBudgetStaffCostEntryList()).toBudgetStaffCostEntryDTOList()

    override fun getBudgetOptions(partnerId: Long): ProjectPartnerBudgetOptionsDto =
        getBudgetOptions.getBudgetOptions(partnerId)?.toProjectPartnerBudgetOptionsDto()
            ?: ProjectPartnerBudgetOptionsDto()

    override fun updateBudgetOptions(partnerId: Long, budgetOptionsDto: ProjectPartnerBudgetOptionsDto) =
        updateBudgetOptions.updateBudgetOptions(partnerId, budgetOptionsDto.toProjectPartnerBudgetOptions(partnerId))

    override fun getBudgetTravel(partnerId: Long) =
        getBudgetTravelAndAccommodationCosts.getBudgetTravelAndAccommodationCosts(partnerId).toBudgetTravelAndAccommodationCostsEntryDTOList()

    override fun updateBudgetTravel(partnerId: Long, travelAndAccommodationCosts: List<BudgetTravelAndAccommodationCostEntryDTO>) =
        updateBudgetTravelAndAccommodationCosts.updateBudgetTravelAndAccommodationCosts(partnerId, travelAndAccommodationCosts.toBudgetTravelAndAccommodationCostEntryList()).toBudgetTravelAndAccommodationCostsEntryDTOList()

    override fun getBudgetExternal(partnerId: Long) =
        getBudgetExternalExpertiseAndServicesCosts.getBudgetGeneralCosts(partnerId).toBudgetGeneralCostsEntryDTOList()

    override fun updateBudgetExternal(partnerId: Long, externals: List<BudgetGeneralCostEntryDTO>) =
        updateBudgetExternalExpertiseAndServicesCosts.updateBudgetGeneralCosts(partnerId, externals.toBudgetGeneralCostEntryList()).toBudgetGeneralCostsEntryDTOList()

    override fun getBudgetEquipment(partnerId: Long) =
        getBudgetEquipmentCosts.getBudgetGeneralCosts(partnerId).toBudgetGeneralCostsEntryDTOList()

    override fun updateBudgetEquipment(partnerId: Long, equipment: List<BudgetGeneralCostEntryDTO>) =
        updateBudgetEquipmentCosts.updateBudgetGeneralCosts(partnerId, equipment.toBudgetGeneralCostEntryList()).toBudgetGeneralCostsEntryDTOList()

    override fun getBudgetInfrastructure(partnerId: Long) =
        getBudgetInfrastructureAndWorksCosts.getBudgetGeneralCosts(partnerId).toBudgetGeneralCostsEntryDTOList()

    override fun updateBudgetInfrastructure(partnerId: Long, infrastructures: List<BudgetGeneralCostEntryDTO>) =
        updateBudgetInfrastructureAndWorksCosts.updateBudgetGeneralCosts(partnerId, infrastructures.toBudgetGeneralCostEntryList()).toBudgetGeneralCostsEntryDTOList()

    override fun getTotal(partnerId: Long) =
        getBudgetTotalCost.getBudgetTotalCost(partnerId)

    override fun getProjectPartnerCoFinancing(partnerId: Long): ProjectPartnerCoFinancingAndContributionOutputDTO =
        getCoFinancing.getCoFinancing(partnerId).toDto()

    override fun updateProjectPartnerCoFinancing(partnerId: Long, partnerCoFinancing: ProjectPartnerCoFinancingAndContributionInputDTO) =
        updateCoFinancing.updateCoFinancing(
            partnerId,
            partnerCoFinancing.finances.toFinancingModel(),
            partnerCoFinancing.partnerContributions.toContributionModel()
        ).toDto()

}
