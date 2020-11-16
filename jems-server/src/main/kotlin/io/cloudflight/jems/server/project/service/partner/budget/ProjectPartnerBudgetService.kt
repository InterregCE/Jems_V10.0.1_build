package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.api.project.dto.partner.budget.InputGeneralBudget
import io.cloudflight.jems.api.project.dto.partner.budget.InputStaffCostBudget
import io.cloudflight.jems.api.project.dto.partner.budget.InputTravelBudget
import java.math.BigDecimal

interface ProjectPartnerBudgetService {

    //region StuffCosts

    fun getStaffCosts(partnerId: Long): List<InputStaffCostBudget>

    fun updateStaffCosts(partnerId: Long, staffCosts: List<InputStaffCostBudget>): List<InputStaffCostBudget>
    //endregion StuffCosts

    //region Travel

    fun getTravel(partnerId: Long): List<InputTravelBudget>

    fun updateTravel(partnerId: Long, travel: List<InputTravelBudget>): List<InputTravelBudget>
    //endregion Travel

    //region External

    fun getExternal(partnerId: Long): List<InputGeneralBudget>

    fun updateExternal(partnerId: Long, externals: List<InputGeneralBudget>): List<InputGeneralBudget>
    //endregion External

    //region Equipment

    fun getEquipment(partnerId: Long): List<InputGeneralBudget>

    fun updateEquipment(partnerId: Long, equipments: List<InputGeneralBudget>): List<InputGeneralBudget>
    //endregion Equipment

    //region Infrastructure

    fun getInfrastructure(partnerId: Long): List<InputGeneralBudget>

    fun updateInfrastructure(partnerId: Long, infrastructures: List<InputGeneralBudget>): List<InputGeneralBudget>
    //endregion Infrastructure

    fun getTotal(partnerId: Long): BigDecimal

}
