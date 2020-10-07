package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.api.project.dto.partner.budget.InputBudget

interface ProjectPartnerBudgetService {

    fun getStaffCosts(projectId: Long, partnerId: Long): List<InputBudget>

    fun updateStaffCosts(projectId: Long, partnerId: Long, staffCosts: List<InputBudget>): List<InputBudget>

}
