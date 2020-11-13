package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudgetOptions

interface ProjectPartnerBudgetPersistence {

    fun getBudgetOptions(partnerId: Long): ProjectPartnerBudgetOptions?

    fun getBudgetOptions(partnerIds: Set<Long>): List<ProjectPartnerBudgetOptions>

    fun updateBudgetOptions(partnerId: Long, officeAdministrationFlatRate: Int?, staffCostsFlatRate: Int?)

    fun deleteBudgetOptions(partnerId: Long)

    fun deleteStaffCosts(partnerId: Long)

}
