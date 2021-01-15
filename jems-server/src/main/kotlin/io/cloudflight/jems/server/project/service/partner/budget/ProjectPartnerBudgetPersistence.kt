package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import java.math.BigDecimal

interface ProjectPartnerBudgetPersistence {

    fun getBudgetStaffCosts(partnerId: Long): List<BudgetStaffCostEntry>
    fun getBudgetStaffCostTotal(partnerId: Long): BigDecimal
    fun createOrUpdateBudgetStaffCosts(partnerId: Long, staffCosts: List<BudgetStaffCostEntry>): List<BudgetStaffCostEntry>
    fun deleteAllBudgetStaffCostsExceptFor(partnerId: Long, idsToKeep: List<Long>)
    fun deleteBudgetStaffCosts(partnerId: Long)

    fun getBudgetTravelAndAccommodationCosts(partnerId: Long): List<BudgetTravelAndAccommodationCostEntry>
    fun getBudgetTravelAndAccommodationCostTotal(partnerId: Long): BigDecimal
    fun createOrUpdateBudgetTravelAndAccommodationCosts(partnerId: Long, travelAndAccommodationCosts: List<BudgetTravelAndAccommodationCostEntry>): List<BudgetTravelAndAccommodationCostEntry>
    fun deleteAllBudgetTravelAndAccommodationCostsExceptFor(partnerId: Long, idsToKeep: List<Long>)
    fun deleteTravelAndAccommodationCosts(partnerId: Long)

    fun getBudgetEquipmentCosts(partnerId: Long): List<BudgetGeneralCostEntry>
    fun getBudgetEquipmentCostTotal(partnerId: Long): BigDecimal
    fun deleteAllBudgetEquipmentCostsExceptFor(partnerId: Long, idsToKeep: List<Long>)
    fun createOrUpdateBudgetEquipmentCosts(partnerId: Long, equipmentCosts: List<BudgetGeneralCostEntry>): List<BudgetGeneralCostEntry>
    fun deleteEquipmentCosts(partnerId: Long)

    fun getBudgetExternalExpertiseAndServicesCosts(partnerId: Long): List<BudgetGeneralCostEntry>
    fun getBudgetExternalExpertiseAndServicesCostTotal(partnerId: Long): BigDecimal
    fun deleteAllBudgetExternalExpertiseAndServicesCostsExceptFor(partnerId: Long, idsToKeep: List<Long>)
    fun createOrUpdateBudgetExternalExpertiseAndServicesCosts(partnerId: Long, externalExpertiseAndServicesCosts: List<BudgetGeneralCostEntry>): List<BudgetGeneralCostEntry>
    fun deleteExternalCosts(partnerId: Long)

    fun getBudgetInfrastructureAndWorksCosts(partnerId: Long): List<BudgetGeneralCostEntry>
    fun getBudgetInfrastructureAndWorksCostTotal(partnerId: Long): BigDecimal
    fun deleteAllBudgetInfrastructureAndWorksCostsExceptFor(partnerId: Long, idsToKeep: List<Long>)
    fun createOrUpdateBudgetInfrastructureAndWorksCosts(partnerId: Long, infrastructureAndWorksCosts: List<BudgetGeneralCostEntry>): List<BudgetGeneralCostEntry>
    fun deleteInfrastructureCosts(partnerId: Long)

    fun getBudgetUnitCosts(partnerId: Long): List<BudgetUnitCostEntry>
    fun getBudgetUnitCostTotal(partnerId: Long): BigDecimal
    fun deleteAllUnitCostsExceptFor(partnerId: Long, idsToKeep: Set<Long>)
    fun createOrUpdateBudgetUnitCosts(partnerId: Long, unitCosts: List<BudgetUnitCostEntry>): List<BudgetUnitCostEntry>
    fun deleteUnitCosts(partnerId: Long)
}
