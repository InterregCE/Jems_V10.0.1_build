package io.cloudflight.jems.server.project.controller.partner.budget

import io.cloudflight.jems.api.project.ProjectPartnerBudgetApi
import io.cloudflight.jems.api.project.dto.partner.budget.InputGeneralBudget
import io.cloudflight.jems.api.project.dto.partner.budget.InputStaffCostBudget
import io.cloudflight.jems.api.project.dto.partner.budget.InputTravelBudget
import io.cloudflight.jems.api.project.dto.partner.budget.ProjectPartnerBudgetOptionsDto
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionOutputDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionInputDTO
import io.cloudflight.jems.server.project.authorization.CanReadProjectPartner
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetService
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_options.GetBudgetOptionsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_options.UpdateBudgetOptionsInteractor
import io.cloudflight.jems.server.project.service.partner.cofinancing.get_cofinancing.GetCoFinancingInteractor
import io.cloudflight.jems.server.project.service.partner.cofinancing.toContributionModel
import io.cloudflight.jems.server.project.service.partner.cofinancing.toDto
import io.cloudflight.jems.server.project.service.partner.cofinancing.toFinancingModel
import io.cloudflight.jems.server.project.service.partner.cofinancing.update_cofinancing.UpdateCoFinancingInteractor
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class ProjectPartnerBudgetController(
    private val projectPartnerBudgetService: ProjectPartnerBudgetService,
    private val getBudgetOptionsInteractor: GetBudgetOptionsInteractor,
    private val updateBudgetOptionsInteractor: UpdateBudgetOptionsInteractor,
    private val getCoFinancing: GetCoFinancingInteractor,
    private val updateCoFinancing: UpdateCoFinancingInteractor
) : ProjectPartnerBudgetApi {

    @CanReadProjectPartner
    override fun getBudgetStaffCost(partnerId: Long): List<InputStaffCostBudget> {
        return projectPartnerBudgetService.getStaffCosts(partnerId)
    }

    @CanUpdateProjectPartner
    override fun updateBudgetStaffCost(partnerId: Long, budgetCosts: List<InputStaffCostBudget>): List<InputStaffCostBudget> {
        return projectPartnerBudgetService.updateStaffCosts(partnerId, budgetCosts)
    }

    override fun getBudgetOptions(partnerId: Long): ProjectPartnerBudgetOptionsDto =
        getBudgetOptionsInteractor.getBudgetOptions(partnerId)?.toProjectPartnerBudgetOptionsDto()
            ?: ProjectPartnerBudgetOptionsDto()

    override fun updateBudgetOptions(partnerId: Long, budgetOptionsDto: ProjectPartnerBudgetOptionsDto) =
        updateBudgetOptionsInteractor.updateBudgetOptions(partnerId, budgetOptionsDto.toModel(partnerId))

    @CanReadProjectPartner
    override fun getBudgetTravel(partnerId: Long): List<InputTravelBudget> {
        return projectPartnerBudgetService.getTravel(partnerId)
    }

    @CanUpdateProjectPartner
    override fun updateBudgetTravel(partnerId: Long, travels: List<InputTravelBudget>): List<InputTravelBudget> {
        return projectPartnerBudgetService.updateTravel(partnerId, travels)
    }

    @CanReadProjectPartner
    override fun getBudgetExternal(partnerId: Long): List<InputGeneralBudget> {
        return projectPartnerBudgetService.getExternal(partnerId)
    }

    @CanUpdateProjectPartner
    override fun updateBudgetExternal(partnerId: Long, externals: List<InputGeneralBudget>): List<InputGeneralBudget> {
        return projectPartnerBudgetService.updateExternal(partnerId, externals)
    }

    @CanReadProjectPartner
    override fun getBudgetEquipment(partnerId: Long): List<InputGeneralBudget> {
        return projectPartnerBudgetService.getEquipment(partnerId)
    }

    @CanUpdateProjectPartner
    override fun updateBudgetEquipment(partnerId: Long, equipments: List<InputGeneralBudget>): List<InputGeneralBudget> {
        return projectPartnerBudgetService.updateEquipment(partnerId, equipments)
    }

    @CanReadProjectPartner
    override fun getBudgetInfrastructure(partnerId: Long): List<InputGeneralBudget> {
        return projectPartnerBudgetService.getInfrastructure(partnerId)
    }

    @CanUpdateProjectPartner
    override fun updateBudgetInfrastructure(partnerId: Long, infrastructures: List<InputGeneralBudget>): List<InputGeneralBudget> {
        return projectPartnerBudgetService.updateInfrastructure(partnerId, infrastructures)
    }

    @CanReadProjectPartner
    override fun getTotal(partnerId: Long): BigDecimal {
        return projectPartnerBudgetService.getTotal(partnerId)
    }

    override fun getProjectPartnerCoFinancing(partnerId: Long): ProjectPartnerCoFinancingAndContributionOutputDTO =
        getCoFinancing.getCoFinancing(partnerId).toDto()

    override fun updateProjectPartnerCoFinancing(
        partnerId: Long,
        partnerCoFinancing: ProjectPartnerCoFinancingAndContributionInputDTO
    ): ProjectPartnerCoFinancingAndContributionOutputDTO =
        updateCoFinancing.updateCoFinancing(
            partnerId,
            partnerCoFinancing.finances.toFinancingModel(),
            partnerCoFinancing.partnerContributions.toContributionModel()
        ).toDto()

}
