package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.api.project.dto.partner.budget.InputBudget

interface ProjectPartnerBudgetService {

    //region StuffCosts

    fun getStaffCosts(projectId: Long, partnerId: Long): List<InputBudget>

    fun updateStaffCosts(projectId: Long, partnerId: Long, staffCosts: List<InputBudget>): List<InputBudget>
    //endregion StuffCosts

    //region Travel

    fun getTravel(projectId: Long, partnerId: Long): List<InputBudget>

    fun updateTravel(projectId: Long, partnerId: Long, travel: List<InputBudget>): List<InputBudget>
    //endregion Travel

    //region External

    fun getExternal(projectId: Long, partnerId: Long): List<InputBudget>

    fun updateExternal(projectId: Long, partnerId: Long, externals: List<InputBudget>): List<InputBudget>
    //endregion External

    //region Equipment

    fun getEquipment(projectId: Long, partnerId: Long): List<InputBudget>

    fun updateEquipment(projectId: Long, partnerId: Long, equipments: List<InputBudget>): List<InputBudget>
    //endregion Equipment

    //region Infrastructure

    fun getInfrastructure(projectId: Long, partnerId: Long): List<InputBudget>

    fun updateInfrastructure(projectId: Long, partnerId: Long, infrastructures: List<InputBudget>): List<InputBudget>
    //endregion Infrastructure

}
