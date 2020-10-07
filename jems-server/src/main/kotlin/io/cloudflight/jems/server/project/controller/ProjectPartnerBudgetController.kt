package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.ProjectPartnerBudgetApi
import io.cloudflight.jems.api.project.dto.partner.budget.InputBudget
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

}
