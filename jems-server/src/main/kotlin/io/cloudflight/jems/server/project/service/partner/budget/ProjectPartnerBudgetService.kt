package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.api.project.dto.partner.budget.InputBudget
import java.math.BigDecimal

interface ProjectPartnerBudgetService {

    //region StuffCosts

    fun getStaffCosts(partnerId: Long): List<InputBudget>

    fun updateStaffCosts(partnerId: Long, staffCosts: List<InputBudget>): List<InputBudget>
    //endregion StuffCosts

    //region Travel

    fun getTravel(partnerId: Long): List<InputBudget>

    fun updateTravel(partnerId: Long, travel: List<InputBudget>): List<InputBudget>
    //endregion Travel

    //region External

    fun getExternal(partnerId: Long): List<InputBudget>

    fun updateExternal(partnerId: Long, externals: List<InputBudget>): List<InputBudget>
    //endregion External

    //region Equipment

    fun getEquipment(partnerId: Long): List<InputBudget>

    fun updateEquipment(partnerId: Long, equipments: List<InputBudget>): List<InputBudget>
    //endregion Equipment

    //region Infrastructure

    fun getInfrastructure(partnerId: Long): List<InputBudget>

    fun updateInfrastructure(partnerId: Long, infrastructures: List<InputBudget>): List<InputBudget>
    //endregion Infrastructure

    fun getTotal(partnerId: Long): BigDecimal

}
