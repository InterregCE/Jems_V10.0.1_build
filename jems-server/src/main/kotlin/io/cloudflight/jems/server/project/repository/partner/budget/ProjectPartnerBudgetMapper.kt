package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.entity.BudgetTranslation
import io.cloudflight.jems.server.project.entity.partner.budget.BaseBudgetProperties
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetEquipmentEntity
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetEquipmentTransl
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetExternalEntity
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetExternalTransl
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetGeneralBase
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetInfrastructureEntity
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetInfrastructureTransl
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetStaffCostEntity
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetStaffCostTransl
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetTravelEntity
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetTravelTransl
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.service.partner.model.StaffCostType
import io.cloudflight.jems.server.project.service.partner.model.StaffCostUnitType


fun List<ProjectPartnerBudgetStaffCostEntity>.toBudgetStaffCostEntries() = this.map { it.toBudgetStaffCostEntry() }
fun ProjectPartnerBudgetStaffCostEntity.toBudgetStaffCostEntry() = BudgetStaffCostEntry(
    id = id,
    description = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.budgetTranslation.language, it.description)
    },
    comment = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.budgetTranslation.language, it.comment)
    },
    type = type ?: StaffCostType.NONE,
    unitType = unitType ?: StaffCostUnitType.NONE,
    numberOfUnits = baseProperties.numberOfUnits,
    pricePerUnit = baseProperties.pricePerUnit,
    rowSum = baseProperties.rowSum
)

fun BudgetStaffCostEntry.toProjectPartnerBudgetStaffCostEntity(partnerId: Long) = ProjectPartnerBudgetStaffCostEntity(
    baseProperties = BaseBudgetProperties(partnerId, numberOfUnits, pricePerUnit, rowSum),
    type = type,
    unitType = unitType,
    translatedValues = mutableSetOf(),
    id = id ?: 0L
).apply {
    translatedValues.addAll(
        description.plus(comment)
            .map { it.language }
            .distinct()
            .map { language ->
                ProjectPartnerBudgetStaffCostTransl(
                    budgetTranslation = BudgetTranslation(this, language),
                    description = description.firstOrNull { it.language === language }?.translation ?: "",
                    comment = comment.firstOrNull { it.language === language }?.translation ?: "",
                )
            }.toMutableSet()
    )
}


fun List<ProjectPartnerBudgetTravelEntity>.toBudgetTravelAndAccommodationCostEntries() = this.map { it.toBudgetTravelAndAccommodationCostEntry() }
fun ProjectPartnerBudgetTravelEntity.toBudgetTravelAndAccommodationCostEntry() = BudgetTravelAndAccommodationCostEntry(
    id = id,
    description = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.budgetTranslation.language, it.description)
    },
    unitType = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.budgetTranslation.language, it.unitType)
    },
    numberOfUnits = baseProperties.numberOfUnits,
    pricePerUnit = baseProperties.pricePerUnit,
    rowSum = baseProperties.rowSum
)

fun BudgetTravelAndAccommodationCostEntry.toProjectPartnerBudgetTravelEntity(partnerId: Long) = ProjectPartnerBudgetTravelEntity(
    baseProperties = BaseBudgetProperties(partnerId, numberOfUnits, pricePerUnit, rowSum),
    translatedValues = mutableSetOf(),
    id = id ?: 0L
).apply {
    translatedValues.addAll(
        description.plus(unitType)
            .map { it.language }
            .distinct()
            .map { language ->
                ProjectPartnerBudgetTravelTransl(
                    budgetTranslation = BudgetTranslation(this, language),
                    description = description.firstOrNull { it.language === language }?.translation ?: "",
                    unitType = unitType.firstOrNull { it.language === language }?.translation ?: "",
                )
            }.toMutableSet()
    )
}


fun List<ProjectPartnerBudgetEquipmentEntity>.equipmentEntitiesToBudgetGeneralEntries() = this.map { it.toBudgetGeneralCostEntry() }
fun BudgetGeneralCostEntry.toProjectPartnerBudgetEquipmentEntity(partnerId: Long) = ProjectPartnerBudgetEquipmentEntity(
    id = id ?: 0L,
    investmentId = investmentId,
    baseProperties = BaseBudgetProperties(partnerId, numberOfUnits, pricePerUnit, rowSum),
    translatedValues = mutableSetOf()
).apply {
    translatedValues.addAll(
        description.plus(awardProcedures).plus(unitType)
            .map { it.language }
            .distinct()
            .map { language ->
                ProjectPartnerBudgetEquipmentTransl(
                    budgetTranslation = BudgetTranslation(this, language),
                    description = description.firstOrNull { it.language === language }?.translation ?: "",
                    unitType = unitType.firstOrNull { it.language === language }?.translation ?: "",
                    awardProcedures = awardProcedures.firstOrNull { it.language === language }?.translation ?: "",
                )
            }.toMutableSet()
    )
}


fun List<ProjectPartnerBudgetExternalEntity>.externalEntitiesToBudgetGeneralEntries() = this.map { it.toBudgetGeneralCostEntry() }
fun BudgetGeneralCostEntry.toProjectPartnerBudgetExternalEntity(partnerId: Long) = ProjectPartnerBudgetExternalEntity(
    id = id ?: 0L,
    investmentId = investmentId,
    baseProperties = BaseBudgetProperties(partnerId, numberOfUnits, pricePerUnit, rowSum),
    translatedValues = mutableSetOf()
).apply {
    translatedValues.addAll(
        description.plus(awardProcedures).plus(unitType)
            .map { it.language }
            .distinct()
            .map { language ->
                ProjectPartnerBudgetExternalTransl(
                    budgetTranslation = BudgetTranslation(this, language),
                    description = description.firstOrNull { it.language === language }?.translation ?: "",
                    unitType = unitType.firstOrNull { it.language === language }?.translation ?: "",
                    awardProcedures = awardProcedures.firstOrNull { it.language === language }?.translation ?: "",
                )
            }.toMutableSet()
    )
}


fun List<ProjectPartnerBudgetInfrastructureEntity>.infrastructureEntitiesToBudgetGeneralEntries() = this.map { it.toBudgetGeneralCostEntry() }
fun BudgetGeneralCostEntry.toProjectPartnerBudgetInfrastructureEntity(partnerId: Long) = ProjectPartnerBudgetInfrastructureEntity(
    id = id ?: 0L,
    investmentId = investmentId,
    baseProperties = BaseBudgetProperties(partnerId, numberOfUnits, pricePerUnit, rowSum),
    translatedValues = mutableSetOf()
).apply {
    translatedValues.addAll(
        description.plus(awardProcedures).plus(unitType)
            .map { it.language }
            .distinct()
            .map { language ->
                ProjectPartnerBudgetInfrastructureTransl(
                    budgetTranslation = BudgetTranslation(this, language),
                    description = description.firstOrNull { it.language === language }?.translation ?: "",
                    unitType = unitType.firstOrNull { it.language === language }?.translation ?: "",
                    awardProcedures = awardProcedures.firstOrNull { it.language === language }?.translation ?: "",
                )
            }.toMutableSet()
    )
}


fun ProjectPartnerBudgetGeneralBase.toBudgetGeneralCostEntry() = BudgetGeneralCostEntry(
    id = id,
    description = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.budgetTranslation.language, it.description)
    },
    unitType = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.budgetTranslation.language, it.unitType)
    },
    awardProcedures = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.budgetTranslation.language, it.awardProcedures)
    },
    investmentId = investmentId,
    numberOfUnits = baseProperties.numberOfUnits,
    pricePerUnit = baseProperties.pricePerUnit,
    rowSum = baseProperties.rowSum
)
