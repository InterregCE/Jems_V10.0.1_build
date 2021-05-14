package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import java.math.BigDecimal

interface ProjectPartnerBudgetCostsPersistence {

    fun getBudgetEquipmentCosts(partnerId: Long, version: Int? = null): List<BudgetGeneralCostEntry>
    fun getBudgetEquipmentCostTotal(partnerId: Long, version: Int? = null): BigDecimal

    fun getBudgetExternalExpertiseAndServicesCosts(partnerId: Long, version: Int? = null): List<BudgetGeneralCostEntry>
    fun getBudgetExternalExpertiseAndServicesCostTotal(partnerId: Long, version: Int? = null): BigDecimal

    fun getBudgetInfrastructureAndWorksCosts(partnerId: Long, version: Int? = null): List<BudgetGeneralCostEntry>
    fun getBudgetInfrastructureAndWorksCostTotal(partnerId: Long, version: Int? = null): BigDecimal

    fun getBudgetStaffCosts(partnerId: Long, version: Int? = null): List<BudgetStaffCostEntry>
    fun getBudgetStaffCostTotal(partnerId: Long, version: Int? = null): BigDecimal

    fun getBudgetTravelAndAccommodationCosts(
        partnerId: Long, version: Int? = null
    ): List<BudgetTravelAndAccommodationCostEntry>

    fun getBudgetTravelAndAccommodationCostTotal(partnerId: Long, version: Int? = null): BigDecimal

    fun getBudgetUnitCosts(partnerId: Long, version: Int? = null): List<BudgetUnitCostEntry>
    fun getBudgetUnitCostTotal(partnerId: Long, version: Int? = null): BigDecimal

    fun getBudgetLumpSumsCostTotal(partnerId: Long, version: Int? = null): BigDecimal
}
