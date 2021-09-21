package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions

interface ProjectPartnerBudgetOptionsPersistence {

    fun getBudgetOptions(partnerId: Long, version: String? = null): ProjectPartnerBudgetOptions?
    fun getBudgetOptions(partnerIds: Set<Long>, projectId: Long, version: String? = null): List<ProjectPartnerBudgetOptions>
    fun updateBudgetOptions(partnerId: Long, options: ProjectPartnerBudgetOptions)
    fun deleteBudgetOptions(partnerId: Long)
    fun deleteStaffCosts(partnerId: Long)
    fun deleteExternalCosts(partnerId: Long)
    fun deleteEquipmentCosts(partnerId: Long)
    fun deleteInfrastructureCosts(partnerId: Long)
    fun deleteTravelAndAccommodationCosts(partnerId: Long)
    fun deleteUnitCosts(partnerId: Long)
    fun getProjectCallFlatRateByPartnerId(partnerId: Long): Set<ProjectCallFlatRate>

}
