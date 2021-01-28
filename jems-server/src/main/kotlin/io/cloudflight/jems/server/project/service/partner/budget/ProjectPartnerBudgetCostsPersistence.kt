package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import java.math.BigDecimal

interface ProjectPartnerBudgetCostsPersistence {


    fun getBudgetEquipmentCosts(partnerId: Long): List<BudgetGeneralCostEntry>
    fun getBudgetEquipmentCostTotal(partnerId: Long): BigDecimal

    fun getBudgetExternalExpertiseAndServicesCosts(partnerId: Long): List<BudgetGeneralCostEntry>
    fun getBudgetExternalExpertiseAndServicesCostTotal(partnerId: Long): BigDecimal

    fun getBudgetInfrastructureAndWorksCosts(partnerId: Long): List<BudgetGeneralCostEntry>
    fun getBudgetInfrastructureAndWorksCostTotal(partnerId: Long): BigDecimal

    fun getBudgetStaffCosts(partnerId: Long): List<BudgetStaffCostEntry>
    fun getBudgetStaffCostTotal(partnerId: Long): BigDecimal

    fun getBudgetTravelAndAccommodationCosts(partnerId: Long): List<BudgetTravelAndAccommodationCostEntry>
    fun getBudgetTravelAndAccommodationCostTotal(partnerId: Long): BigDecimal

    fun getBudgetUnitCosts(partnerId: Long): List<BudgetUnitCostEntry>
    fun getBudgetUnitCostTotal(partnerId: Long): BigDecimal

    fun getBudgetLumpSumsCostTotal(partnerId: Long): BigDecimal
}
