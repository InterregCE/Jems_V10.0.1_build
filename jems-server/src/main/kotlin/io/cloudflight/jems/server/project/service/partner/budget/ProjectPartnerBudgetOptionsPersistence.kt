package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudgetOptions

interface ProjectPartnerBudgetOptionsPersistence {

    fun getBudgetOptions(partnerId: Long): ProjectPartnerBudgetOptions?
    fun getBudgetOptions(partnerIds: Set<Long>): List<ProjectPartnerBudgetOptions>
    fun updateBudgetOptions(partnerId: Long, options: ProjectPartnerBudgetOptions)
    fun deleteBudgetOptions(partnerId: Long)
    fun deleteStaffCosts(partnerId: Long)
    fun deleteExternalCosts(partnerId: Long)
    fun deleteEquipmentCosts(partnerId: Long)
    fun deleteInfrastructureCosts(partnerId: Long)
    fun deleteTravelAndAccommodationCosts(partnerId: Long)

}
