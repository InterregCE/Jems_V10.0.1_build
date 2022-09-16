package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.updateProjectUnitCost

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.costoption.getStaticValidationResults
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.costoption.validateUpdateUnitCost
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectFormOnlyBeforeContracted
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.customCostOptions.ProjectUnitCostPersistence
import io.cloudflight.jems.server.project.service.customCostOptions.unitCost.projectUnitCostChanged
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsUpdatePersistence
import io.cloudflight.jems.server.project.service.partner.budget.truncate
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UpdateProjectUnitCost(
    private val programmeUnitCostPersistence: ProgrammeUnitCostPersistence,
    private val projectUnitCostPersistence: ProjectUnitCostPersistence,
    private val projectPersistence: ProjectPersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val generalValidator: GeneralValidatorService,
    private val partnerPersistence: PartnerPersistence,
    private val projectPartnerBudgetCostsPersistence: ProjectPartnerBudgetCostsPersistence,
    private val projectPartnerBudgetCostsUpdatePersistence: ProjectPartnerBudgetCostsUpdatePersistence,
) : UpdateProjectUnitCostInteractor {

    @CanUpdateProjectFormOnlyBeforeContracted
    @Transactional
    @ExceptionWrapper(UpdateProjectUnitCostException::class)
    override fun updateProjectUnitCost(projectId: Long, unitCost: ProgrammeUnitCost): ProgrammeUnitCost {
        unitCost.validateInputFields()
        validateUpdateUnitCost(unitCost)

        val old = projectUnitCostPersistence.getProjectUnitCost(projectId, unitCostId = unitCost.id, null)
        unitCost.projectId = projectId

        val deselectedCategories = getDeselectedCategories(old = old, new = unitCost)
        val coverageTypeChanged = old.isOneCostCategory != unitCost.isOneCostCategory

        if (thereAreImportantChanges(old, new = unitCost) || deselectedCategories.isNotEmpty() || coverageTypeChanged) {
            updateUnitCostUsages(unitCost, deselectedCategories, coverageTypeChanged)
        }

        return programmeUnitCostPersistence.updateUnitCost(unitCost).also {
            auditPublisher.publishEvent(
                projectUnitCostChanged(this, it, projectPersistence.getProjectSummary(projectId))
            )
        }
    }

    private fun ProgrammeUnitCost.validateInputFields() {
        generalValidator.throwIfAnyIsInvalid(
            *getStaticValidationResults(generalValidator).toTypedArray(),
        )
    }

    private fun thereAreImportantChanges(old: ProgrammeUnitCost, new: ProgrammeUnitCost) =
        numbersDiffer(old.costPerUnit, new.costPerUnit) || old.type != new.type

    private fun numbersDiffer(first: BigDecimal?, second: BigDecimal?) =
        first!!.compareTo(second!!) != 0

    private fun getDeselectedCategories(old: ProgrammeUnitCost, new: ProgrammeUnitCost) =
        old.categories.minus(new.categories)

    private fun updateUnitCostUsages(
        unitCost: ProgrammeUnitCost,
        deselectedCategories: Set<BudgetCategory>,
        coverageTypeChanged: Boolean,
    ) {
        val partnerIds = partnerPersistence.findTop30ByProjectId(unitCost.projectId!!).mapTo(HashSet()) { it.id }

        val unitCostForStaffCostsTable =
            if (coverageTypeChanged || deselectedCategories.contains(BudgetCategory.StaffCosts)) null else unitCost
        val unitCostForEquipmentCostsTable =
            if (coverageTypeChanged || deselectedCategories.contains(BudgetCategory.EquipmentCosts)) null else unitCost
        val unitCostForExternalCostsTable =
            if (coverageTypeChanged || deselectedCategories.contains(BudgetCategory.ExternalCosts)) null else unitCost
        val unitCostForInfrastructureCostsTable =
            if (coverageTypeChanged || deselectedCategories.contains(BudgetCategory.InfrastructureCosts)) null else unitCost
        val unitCostForTravelCostsTable =
            if (coverageTypeChanged || deselectedCategories.contains(BudgetCategory.TravelAndAccommodationCosts)) null else unitCost
        val unitCostForMultiCostsTable =
            if (coverageTypeChanged) null else unitCost

        partnerIds.forEach { partnerId ->
            updateStaffCosts(unitCost.projectId!!, partnerId =  partnerId, unitCost.id, unitCostForStaffCostsTable)
            updateEquipmentCosts(unitCost.projectId!!, partnerId =  partnerId, unitCost.id, unitCostForEquipmentCostsTable)
            updateExternalCosts(unitCost.projectId!!, partnerId =  partnerId, unitCost.id, unitCostForExternalCostsTable)
            updateInfrastructureCosts(unitCost.projectId!!, partnerId =  partnerId, unitCost.id, unitCostForInfrastructureCostsTable)
            updateTravelCosts(unitCost.projectId!!, partnerId =  partnerId, unitCost.id, unitCostForTravelCostsTable)
            updateMultiCategoriesUnitCosts(unitCost.projectId!!, partnerId =  partnerId, unitCost.id, unitCostForMultiCostsTable)
        }
    }

    private fun updateStaffCosts(projectId: Long, partnerId: Long, oldUnitCostId: Long, unitCost: ProgrammeUnitCost?) {
        val staffCosts = projectPartnerBudgetCostsPersistence.getBudgetStaffCosts(partnerId)
            .let {
                if (unitCost == null)
                    it.filter { it.unitCostId != oldUnitCostId }
                else
                    it.onEach {
                        if (it.unitCostId == unitCost.id) {
                            it.pricePerUnit = unitCost.costPerUnit!!
                            it.rowSum = it.pricePerUnit.multiply(it.numberOfUnits).truncate()
                            it.unitType = unitCost.type
                        }
                    }
            }
        val idsToKeep = staffCosts.mapNotNullTo(HashSet()) { it.id }
        projectPartnerBudgetCostsUpdatePersistence.deleteAllBudgetStaffCostsExceptFor(partnerId, idsToKeep)
        projectPartnerBudgetCostsUpdatePersistence.createOrUpdateBudgetStaffCosts(projectId, partnerId, staffCosts)
    }

    private fun updateEquipmentCosts(projectId: Long, partnerId: Long, oldUnitCostId: Long, unitCost: ProgrammeUnitCost?) {
        val equipmentCosts = projectPartnerBudgetCostsPersistence.getBudgetEquipmentCosts(partnerId)
            .let {
                if (unitCost == null)
                    it.filter { it.unitCostId != oldUnitCostId }
                else
                    it.onEach {
                        if (it.unitCostId == unitCost.id) {
                            it.pricePerUnit = unitCost.costPerUnit!!
                            it.rowSum = it.pricePerUnit.multiply(it.numberOfUnits).truncate()
                            it.unitType = unitCost.type
                        }
                    }
            }
        val idsToKeep = equipmentCosts.mapNotNullTo(HashSet()) { it.id }
        projectPartnerBudgetCostsUpdatePersistence.deleteAllBudgetEquipmentCostsExceptFor(partnerId, idsToKeep)
        projectPartnerBudgetCostsUpdatePersistence.createOrUpdateBudgetEquipmentCosts(projectId, partnerId, equipmentCosts)
    }

    private fun updateExternalCosts(projectId: Long, partnerId: Long, oldUnitCostId: Long, unitCost: ProgrammeUnitCost?) {
        val externalCosts = projectPartnerBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(partnerId)
            .let {
                if (unitCost == null)
                    it.filter { it.unitCostId != oldUnitCostId }
                else
                    it.onEach {
                        if (it.unitCostId == unitCost.id) {
                            it.pricePerUnit = unitCost.costPerUnit!!
                            it.rowSum = it.pricePerUnit.multiply(it.numberOfUnits).truncate()
                            it.unitType = unitCost.type
                        }
                    }
            }
        val idsToKeep = externalCosts.mapNotNullTo(HashSet()) { it.id }
        projectPartnerBudgetCostsUpdatePersistence.deleteAllBudgetExternalExpertiseAndServicesCostsExceptFor(partnerId, idsToKeep)
        projectPartnerBudgetCostsUpdatePersistence.createOrUpdateBudgetExternalExpertiseAndServicesCosts(projectId, partnerId, externalCosts)
    }

    private fun updateInfrastructureCosts(projectId: Long, partnerId: Long, oldUnitCostId: Long, unitCost: ProgrammeUnitCost?) {
        val infrastructureCosts = projectPartnerBudgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(partnerId)
            .let {
                if (unitCost == null)
                    it.filter { it.unitCostId != oldUnitCostId }
                else
                    it.onEach {
                        if (it.unitCostId == unitCost.id) {
                            it.pricePerUnit = unitCost.costPerUnit!!
                            it.rowSum = it.pricePerUnit.multiply(it.numberOfUnits).truncate()
                            it.unitType = unitCost.type
                        }
                    }
            }
        val idsToKeep = infrastructureCosts.mapNotNullTo(HashSet()) { it.id }
        projectPartnerBudgetCostsUpdatePersistence.deleteAllBudgetInfrastructureAndWorksCostsExceptFor(partnerId, idsToKeep)
        projectPartnerBudgetCostsUpdatePersistence.createOrUpdateBudgetInfrastructureAndWorksCosts(projectId, partnerId, infrastructureCosts)
    }

    private fun updateTravelCosts(projectId: Long, partnerId: Long, oldUnitCostId: Long, unitCost: ProgrammeUnitCost?) {
        val travelCosts = projectPartnerBudgetCostsPersistence.getBudgetTravelAndAccommodationCosts(partnerId)
            .let {
                if (unitCost == null)
                    it.filter { it.unitCostId != oldUnitCostId }
                else
                    it.onEach {
                        if (it.unitCostId == unitCost.id) {
                            it.pricePerUnit = unitCost.costPerUnit!!
                            it.rowSum = it.pricePerUnit.multiply(it.numberOfUnits).truncate()
                            it.unitType = unitCost.type
                        }
                    }
            }
        val idsToKeep = travelCosts.mapNotNullTo(HashSet()) { it.id }
        projectPartnerBudgetCostsUpdatePersistence.deleteAllBudgetTravelAndAccommodationCostsExceptFor(partnerId, idsToKeep)
        projectPartnerBudgetCostsUpdatePersistence.createOrUpdateBudgetTravelAndAccommodationCosts(projectId, partnerId, travelCosts)
    }

    private fun updateMultiCategoriesUnitCosts(projectId: Long, partnerId: Long, oldUnitCostId: Long, unitCost: ProgrammeUnitCost?) {
        val multiCatCosts = projectPartnerBudgetCostsPersistence.getBudgetUnitCosts(partnerId)
            .let {
                if (unitCost == null)
                    it.filter { it.unitCostId != oldUnitCostId }
                else
                    it.onEach {
                        if (it.unitCostId == unitCost.id) {
                            it.rowSum = unitCost.costPerUnit!!.multiply(it.numberOfUnits).truncate()
                        }
                    }
            }
        val idsToKeep = multiCatCosts.mapNotNullTo(HashSet()) { it.id }
        projectPartnerBudgetCostsUpdatePersistence.deleteAllUnitCostsExceptFor(partnerId, idsToKeep)
        projectPartnerBudgetCostsUpdatePersistence.createOrUpdateBudgetUnitCosts(projectId, partnerId, multiCatCosts)
    }

}
