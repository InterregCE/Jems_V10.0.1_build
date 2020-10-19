package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.ProjectPartnerBudgetApi
import io.cloudflight.jems.api.project.dto.partner.budget.InputBudget
import io.cloudflight.jems.api.project.dto.partner.budget.InputFlatRate
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerBudgetController(
    private val projectPartnerBudgetService: ProjectPartnerBudgetService
) : ProjectPartnerBudgetApi {

    @PreAuthorize("@projectAuthorization.canReadProject(#projectId)")
    override fun getBudgetStaffCost(projectId: Long, partnerId: Long): List<InputBudget> {
        return projectPartnerBudgetService.getStaffCosts(projectId, partnerId)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun updateBudgetStaffCost(projectId: Long, partnerId: Long, budgetCosts: List<InputBudget>): List<InputBudget> {
        return projectPartnerBudgetService.updateStaffCosts(projectId, partnerId, budgetCosts)
    }

    @PreAuthorize("@projectAuthorization.canReadProject(#projectId)")
    override fun getBudgetTravel(projectId: Long, partnerId: Long): List<InputBudget> {
        return projectPartnerBudgetService.getTravel(projectId, partnerId)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun updateBudgetTravel(projectId: Long, partnerId: Long, travels: List<InputBudget>): List<InputBudget> {
        return projectPartnerBudgetService.updateTravel(projectId, partnerId, travels)
    }

    @PreAuthorize("@projectAuthorization.canReadProject(#projectId)")
    override fun getBudgetExternal(projectId: Long, partnerId: Long): List<InputBudget> {
        return projectPartnerBudgetService.getExternal(projectId, partnerId)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun updateBudgetExternal(projectId: Long, partnerId: Long, externals: List<InputBudget>): List<InputBudget> {
        return projectPartnerBudgetService.updateExternal(projectId, partnerId, externals)
    }

    @PreAuthorize("@projectAuthorization.canReadProject(#projectId)")
    override fun getBudgetEquipment(projectId: Long, partnerId: Long): List<InputBudget> {
        return projectPartnerBudgetService.getEquipment(projectId, partnerId)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun updateBudgetEquipment(projectId: Long, partnerId: Long, equipments: List<InputBudget>): List<InputBudget> {
        return projectPartnerBudgetService.updateEquipment(projectId, partnerId, equipments)
    }

    @PreAuthorize("@projectAuthorization.canReadProject(#projectId)")
    override fun getBudgetInfrastructure(projectId: Long, partnerId: Long): List<InputBudget> {
        return projectPartnerBudgetService.getInfrastructure(projectId, partnerId)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun updateBudgetInfrastructure(projectId: Long, partnerId: Long, infrastructures: List<InputBudget>): List<InputBudget> {
        return projectPartnerBudgetService.updateInfrastructure(projectId, partnerId, infrastructures)
    }

    @PreAuthorize("@projectAuthorization.canReadProject(#projectId)")
    override fun getOfficeAdministrationFlatRate(projectId: Long, partnerId: Long): Int? {
        return projectPartnerBudgetService.getOfficeAdministrationFlatRate(projectId, partnerId)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun updateOfficeAdministrationFlatRate(projectId: Long, partnerId: Long, flatRate: InputFlatRate): Int? {
        return projectPartnerBudgetService.updateOfficeAdministrationFlatRate(projectId, partnerId, flatRate.value)
    }

}
