package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetSpfCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import java.math.BigDecimal

interface ProjectPartnerBudgetCostsPersistence {

    fun getBudgetEquipmentCosts(partnerId: Long, version: String? = null): List<BudgetGeneralCostEntry>
    fun getBudgetEquipmentCostTotal(partnerId: Long, version: String? = null): BigDecimal

    fun getBudgetExternalExpertiseAndServicesCosts(partnerId: Long, version: String? = null): List<BudgetGeneralCostEntry>
    fun getBudgetExternalExpertiseAndServicesCostTotal(partnerId: Long, version: String? = null): BigDecimal

    fun getBudgetInfrastructureAndWorksCosts(partnerId: Long, version: String? = null): List<BudgetGeneralCostEntry>
    fun getBudgetInfrastructureAndWorksCostTotal(partnerId: Long, version: String? = null): BigDecimal

    fun getBudgetStaffCosts(partnerId: Long, version: String? = null): List<BudgetStaffCostEntry>
    fun getBudgetStaffCostTotal(partnerId: Long, version: String? = null): BigDecimal

    fun getBudgetTravelAndAccommodationCosts(
        partnerId: Long, version: String? = null
    ): List<BudgetTravelAndAccommodationCostEntry>

    fun getBudgetTravelAndAccommodationCostTotal(partnerId: Long, version: String? = null): BigDecimal

    fun getBudgetSpfCosts(partnerId: Long, version: String? = null): List<BudgetSpfCostEntry>
    fun getBudgetSpfCostTotal(partnerId: Long, version: String? = null): BigDecimal

    fun getBudgetUnitCosts(partnerId: Long, version: String? = null): List<BudgetUnitCostEntry>
    fun getBudgetUnitCostTotal(partnerId: Long, version: String? = null): BigDecimal

    fun getBudgetLumpSumsCostTotal(partnerId: Long, version: String? = null): BigDecimal

    fun isUnitCostUsed(unitCostId: Long): Boolean

}
