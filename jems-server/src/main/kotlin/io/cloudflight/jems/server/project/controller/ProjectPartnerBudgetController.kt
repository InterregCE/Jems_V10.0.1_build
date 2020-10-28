package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.ProjectPartnerBudgetApi
import io.cloudflight.jems.api.project.dto.partner.budget.InputBudget
import io.cloudflight.jems.api.project.dto.partner.budget.InputFlatRate
import io.cloudflight.jems.server.project.authorization.CanReadProjectPartner
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetService
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class ProjectPartnerBudgetController(
    private val projectPartnerBudgetService: ProjectPartnerBudgetService
) : ProjectPartnerBudgetApi {

    @CanReadProjectPartner
    override fun getBudgetStaffCost(partnerId: Long): List<InputBudget> {
        return projectPartnerBudgetService.getStaffCosts(partnerId)
    }

    @CanUpdateProjectPartner
    override fun updateBudgetStaffCost(partnerId: Long, budgetCosts: List<InputBudget>): List<InputBudget> {
        return projectPartnerBudgetService.updateStaffCosts(partnerId, budgetCosts)
    }

    @CanReadProjectPartner
    override fun getBudgetTravel(partnerId: Long): List<InputBudget> {
        return projectPartnerBudgetService.getTravel(partnerId)
    }

    @CanUpdateProjectPartner
    override fun updateBudgetTravel(partnerId: Long, travels: List<InputBudget>): List<InputBudget> {
        return projectPartnerBudgetService.updateTravel(partnerId, travels)
    }

    @CanReadProjectPartner
    override fun getBudgetExternal(partnerId: Long): List<InputBudget> {
        return projectPartnerBudgetService.getExternal(partnerId)
    }

    @CanUpdateProjectPartner
    override fun updateBudgetExternal(partnerId: Long, externals: List<InputBudget>): List<InputBudget> {
        return projectPartnerBudgetService.updateExternal(partnerId, externals)
    }

    @CanReadProjectPartner
    override fun getBudgetEquipment(partnerId: Long): List<InputBudget> {
        return projectPartnerBudgetService.getEquipment(partnerId)
    }

    @CanUpdateProjectPartner
    override fun updateBudgetEquipment(partnerId: Long, equipments: List<InputBudget>): List<InputBudget> {
        return projectPartnerBudgetService.updateEquipment(partnerId, equipments)
    }

    @CanReadProjectPartner
    override fun getBudgetInfrastructure(partnerId: Long): List<InputBudget> {
        return projectPartnerBudgetService.getInfrastructure(partnerId)
    }

    @CanUpdateProjectPartner
    override fun updateBudgetInfrastructure(partnerId: Long, infrastructures: List<InputBudget>): List<InputBudget> {
        return projectPartnerBudgetService.updateInfrastructure(partnerId, infrastructures)
    }

    @CanReadProjectPartner
    override fun getOfficeAdministrationFlatRate(partnerId: Long): Int? {
        return projectPartnerBudgetService.getOfficeAdministrationFlatRate(partnerId)
    }

    @CanUpdateProjectPartner
    override fun updateOfficeAdministrationFlatRate(partnerId: Long, flatRate: InputFlatRate): Int? {
        return projectPartnerBudgetService.updateOfficeAdministrationFlatRate(partnerId, flatRate.value)
    }

    @CanReadProjectPartner
    override fun getTotal(partnerId: Long): BigDecimal {
        return projectPartnerBudgetService.getTotal(partnerId)
    }

}
