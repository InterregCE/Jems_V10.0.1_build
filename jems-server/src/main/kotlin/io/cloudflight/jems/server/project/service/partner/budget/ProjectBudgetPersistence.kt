package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetOptions

interface ProjectBudgetPersistence {

    fun getBudgetOptions(partnerId: Long): ProjectPartnerBudgetOptions?

    fun updateBudgetOptions(partnerId: Long, officeAdministrationFlatRate: Int?, staffCostsFlatRate: Int?)

    fun deleteBudgetOptions(partnerId: Long)

    fun deleteStaffCosts(partnerId: Long)

}
