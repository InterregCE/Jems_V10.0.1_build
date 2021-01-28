package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry

interface ProjectPartnerBudgetCostsUpdatePersistence {

    fun deleteAllBudgetEquipmentCostsExceptFor(partnerId: Long, idsToKeep: Set<Long>)
    fun createOrUpdateBudgetEquipmentCosts(
        projectId: Long,
        partnerId: Long,
        equipmentCosts: Set<BudgetGeneralCostEntry>
    ): List<BudgetGeneralCostEntry>

    fun deleteAllBudgetExternalExpertiseAndServicesCostsExceptFor(partnerId: Long, idsToKeep: Set<Long>)
    fun createOrUpdateBudgetExternalExpertiseAndServicesCosts(
        projectId: Long,
        partnerId: Long,
        externalExpertiseAndServicesCosts: Set<BudgetGeneralCostEntry>
    ): List<BudgetGeneralCostEntry>


    fun deleteAllBudgetInfrastructureAndWorksCostsExceptFor(partnerId: Long, idsToKeep: Set<Long>)
    fun createOrUpdateBudgetInfrastructureAndWorksCosts(
        projectId: Long,
        partnerId: Long,
        infrastructureAndWorksCosts: Set<BudgetGeneralCostEntry>
    ): List<BudgetGeneralCostEntry>

    fun createOrUpdateBudgetStaffCosts(
        projectId: Long,
        partnerId: Long,
        staffCosts: Set<BudgetStaffCostEntry>
    ): List<BudgetStaffCostEntry>

    fun deleteAllBudgetStaffCostsExceptFor(partnerId: Long, idsToKeep: Set<Long>)

    fun createOrUpdateBudgetTravelAndAccommodationCosts(
        projectId: Long,
        partnerId: Long,
        travelAndAccommodationCosts: Set<BudgetTravelAndAccommodationCostEntry>
    ): List<BudgetTravelAndAccommodationCostEntry>

    fun deleteAllBudgetTravelAndAccommodationCostsExceptFor(partnerId: Long, idsToKeep: Set<Long>)

    fun deleteAllUnitCostsExceptFor(partnerId: Long, idsToKeep: Set<Long>)
    fun createOrUpdateBudgetUnitCosts(
        projectId: Long,
        partnerId: Long,
        unitCosts: Set<BudgetUnitCostEntry>
    ): List<BudgetUnitCostEntry>
}
