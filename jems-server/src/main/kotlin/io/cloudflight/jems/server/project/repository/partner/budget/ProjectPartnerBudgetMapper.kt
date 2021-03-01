package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.partner.budget.BaseBudgetProperties
import io.cloudflight.jems.server.project.entity.partner.budget.BudgetPeriodId
import io.cloudflight.jems.server.project.entity.partner.budget.BudgetTranslation
import io.cloudflight.jems.server.project.entity.partner.budget.general.ProjectPartnerBudgetGeneralBase
import io.cloudflight.jems.server.project.entity.partner.budget.general.equipment.ProjectPartnerBudgetEquipmentEntity
import io.cloudflight.jems.server.project.entity.partner.budget.general.equipment.ProjectPartnerBudgetEquipmentPeriodEntity
import io.cloudflight.jems.server.project.entity.partner.budget.general.equipment.ProjectPartnerBudgetEquipmentTranslEntity
import io.cloudflight.jems.server.project.entity.partner.budget.general.external.ProjectPartnerBudgetExternalEntity
import io.cloudflight.jems.server.project.entity.partner.budget.general.external.ProjectPartnerBudgetExternalPeriodEntity
import io.cloudflight.jems.server.project.entity.partner.budget.general.external.ProjectPartnerBudgetExternalTranslEntity
import io.cloudflight.jems.server.project.entity.partner.budget.general.infrastructure.ProjectPartnerBudgetInfrastructureEntity
import io.cloudflight.jems.server.project.entity.partner.budget.general.infrastructure.ProjectPartnerBudgetInfrastructurePeriodEntity
import io.cloudflight.jems.server.project.entity.partner.budget.general.infrastructure.ProjectPartnerBudgetInfrastructureTranslEntity
import io.cloudflight.jems.server.project.entity.partner.budget.staff_cost.ProjectPartnerBudgetStaffCostEntity
import io.cloudflight.jems.server.project.entity.partner.budget.staff_cost.ProjectPartnerBudgetStaffCostPeriodEntity
import io.cloudflight.jems.server.project.entity.partner.budget.staff_cost.ProjectPartnerBudgetStaffCostTranslEntity
import io.cloudflight.jems.server.project.entity.partner.budget.travel.ProjectPartnerBudgetTravelEntity
import io.cloudflight.jems.server.project.entity.partner.budget.travel.ProjectPartnerBudgetTravelPeriodEntity
import io.cloudflight.jems.server.project.entity.partner.budget.travel.ProjectPartnerBudgetTravelTranslEntity
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetPeriod
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry


fun List<ProjectPartnerBudgetStaffCostEntity>.toBudgetStaffCostEntries() = this.map { it.toBudgetStaffCostEntry() }
fun ProjectPartnerBudgetStaffCostEntity.toBudgetStaffCostEntry() = BudgetStaffCostEntry(
    id = id,
    description = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.budgetTranslation.language, it.description)
    },
    comment = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.budgetTranslation.language, it.comment)
    },
    budgetPeriods = budgetPeriodEntities.map { BudgetPeriod(it.budgetPeriodId.period.id.number, it.amount) }
        .toMutableSet(),
    unitType = unitType,
    type = type,
    numberOfUnits = baseProperties.numberOfUnits,
    pricePerUnit = pricePerUnit,
    rowSum = baseProperties.rowSum
)

fun Set<BudgetStaffCostEntry>.toProjectPartnerBudgetStaffCostEntities(
    partnerId: Long,
    projectPeriodEntityResolver: (Int) -> ProjectPeriodEntity
) = map { it.toProjectPartnerBudgetStaffCostEntity(partnerId, projectPeriodEntityResolver) }

fun BudgetStaffCostEntry.toProjectPartnerBudgetStaffCostEntity(
    partnerId: Long,
    projectPeriodEntityReferenceResolver: (Int) -> ProjectPeriodEntity
) =
    ProjectPartnerBudgetStaffCostEntity(
        baseProperties = BaseBudgetProperties(partnerId, numberOfUnits, rowSum!!),
        pricePerUnit = pricePerUnit,
        type = type,
        unitType = unitType,
        translatedValues = mutableSetOf(),
        budgetPeriodEntities = mutableSetOf(),
        id = id ?: 0L
    ).apply {
        translatedValues.addAll(
            description.plus(comment)
                .mapTo(HashSet()) { it.language }
                .map { language ->
                    ProjectPartnerBudgetStaffCostTranslEntity(
                        budgetTranslation = BudgetTranslation(this, language),
                        description = description.firstOrNull { it.language == language }?.translation ?: "",
                        comment = comment.firstOrNull { it.language == language }?.translation ?: ""
                    )
                }.toMutableSet()
        )

        budgetPeriodEntities.addAll(budgetPeriods.map {
            ProjectPartnerBudgetStaffCostPeriodEntity(
                BudgetPeriodId(
                    this,
                    projectPeriodEntityReferenceResolver.invoke(it.number)
                ),
                it.amount
            )
        }.toMutableSet())
    }

fun List<ProjectPartnerBudgetTravelEntity>.toBudgetTravelAndAccommodationCostEntries() =
    this.map { it.toBudgetTravelAndAccommodationCostEntry() }

fun ProjectPartnerBudgetTravelEntity.toBudgetTravelAndAccommodationCostEntry() = BudgetTravelAndAccommodationCostEntry(
    id = id,
    description = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.budgetTranslation.language, it.description)
    },
    unitType = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.budgetTranslation.language, it.unitType)
    },
    budgetPeriods = budgetPeriodEntities.map { BudgetPeriod(it.budgetPeriodId.period.id.number, it.amount) }
        .toMutableSet(),
    numberOfUnits = baseProperties.numberOfUnits,
    pricePerUnit = pricePerUnit,
    rowSum = baseProperties.rowSum
)

fun Set<BudgetTravelAndAccommodationCostEntry>.toProjectPartnerBudgetTravelEntities(
    partnerId: Long,
    projectPeriodEntityReferenceResolver: (Int) -> ProjectPeriodEntity
) = map { it.toProjectPartnerBudgetTravelEntity(partnerId, projectPeriodEntityReferenceResolver) }

fun BudgetTravelAndAccommodationCostEntry.toProjectPartnerBudgetTravelEntity(
    partnerId: Long,
    projectPeriodEntityReferenceResolver: (Int) -> ProjectPeriodEntity
) =
    ProjectPartnerBudgetTravelEntity(
        baseProperties = BaseBudgetProperties(partnerId, numberOfUnits, rowSum!!),
        pricePerUnit = pricePerUnit,
        translatedValues = mutableSetOf(),
        budgetPeriodEntities = mutableSetOf(),
        id = id ?: 0L
    ).apply {
        translatedValues.addAll(
            description.plus(unitType)
                .mapTo(HashSet()) { it.language }
                .map { language ->
                    ProjectPartnerBudgetTravelTranslEntity(
                        budgetTranslation = BudgetTranslation(this, language),
                        description = description.firstOrNull { it.language == language }?.translation ?: "",
                        unitType = unitType.firstOrNull { it.language == language }?.translation ?: ""
                    )
                }.toMutableSet()
        )

        budgetPeriodEntities.addAll(budgetPeriods.map {
            ProjectPartnerBudgetTravelPeriodEntity(
                BudgetPeriodId(
                    this,
                    projectPeriodEntityReferenceResolver.invoke(it.number)
                ),
                it.amount
            )
        }.toMutableSet())
    }


fun List<ProjectPartnerBudgetEquipmentEntity>.equipmentEntitiesToBudgetGeneralEntries() =
    this.map { it.toBudgetGeneralCostEntry() }

fun Set<BudgetGeneralCostEntry>.toProjectPartnerBudgetEquipmentEntity(
    partnerId: Long,
    projectPeriodEntityReferenceResolver: (Int) -> ProjectPeriodEntity
) = map { it.toProjectPartnerBudgetEquipmentEntity(partnerId, projectPeriodEntityReferenceResolver) }

fun BudgetGeneralCostEntry.toProjectPartnerBudgetEquipmentEntity(
    partnerId: Long,
    projectPeriodEntityReferenceResolver: (Int) -> ProjectPeriodEntity
) =
    ProjectPartnerBudgetEquipmentEntity(
        id = id ?: 0L,
        investmentId = investmentId,
        baseProperties = BaseBudgetProperties(partnerId, numberOfUnits, rowSum!!),
        pricePerUnit = pricePerUnit,
        budgetPeriodEntities = mutableSetOf(),
        translatedValues = mutableSetOf()
    ).apply {
        translatedValues.addAll(
            description.plus(awardProcedures).plus(unitType)
                .mapTo(HashSet()) { it.language }
                .map { language ->
                    ProjectPartnerBudgetEquipmentTranslEntity(
                        budgetTranslation = BudgetTranslation(this, language),
                        description = description.firstOrNull { it.language == language }?.translation ?: "",
                        unitType = unitType.firstOrNull { it.language == language }?.translation ?: "",
                        awardProcedures = awardProcedures.firstOrNull { it.language == language }?.translation ?: ""
                    )
                }.toMutableSet()
        )

        budgetPeriodEntities.addAll(budgetPeriods.map {
            ProjectPartnerBudgetEquipmentPeriodEntity(
                BudgetPeriodId(
                    this,
                    projectPeriodEntityReferenceResolver.invoke(it.number)
                ),
                it.amount
            )
        }.toMutableSet())
    }


fun List<ProjectPartnerBudgetExternalEntity>.externalEntitiesToBudgetGeneralEntries() =
    this.map { it.toBudgetGeneralCostEntry() }

fun Set<BudgetGeneralCostEntry>.toProjectPartnerBudgetExternalEntities(
    partnerId: Long,
    projectPeriodEntityReferenceResolver: (Int) -> ProjectPeriodEntity
) = map { it.toProjectPartnerBudgetExternalEntity(partnerId, projectPeriodEntityReferenceResolver) }

fun BudgetGeneralCostEntry.toProjectPartnerBudgetExternalEntity(
    partnerId: Long,
    projectPeriodEntityReferenceResolver: (Int) -> ProjectPeriodEntity
) =
    ProjectPartnerBudgetExternalEntity(
        id = id ?: 0L,
        investmentId = investmentId,
        baseProperties = BaseBudgetProperties(partnerId, numberOfUnits, rowSum!!),
        pricePerUnit = pricePerUnit,
        budgetPeriodEntities = mutableSetOf(),
        translatedValues = mutableSetOf()
    ).apply {
        translatedValues.addAll(
            description.plus(awardProcedures).plus(unitType)
                .mapTo(HashSet()) { it.language }
                .map { language ->
                    ProjectPartnerBudgetExternalTranslEntity(
                        budgetTranslation = BudgetTranslation(this, language),
                        description = description.firstOrNull { it.language == language }?.translation ?: "",
                        unitType = unitType.firstOrNull { it.language == language }?.translation ?: "",
                        awardProcedures = awardProcedures.firstOrNull { it.language == language }?.translation ?: ""
                    )
                }.toMutableSet()
        )

        budgetPeriodEntities.addAll(budgetPeriods.map {
            ProjectPartnerBudgetExternalPeriodEntity(
                BudgetPeriodId(
                    this,
                    projectPeriodEntityReferenceResolver.invoke(it.number)
                ),
                it.amount
            )
        }.toMutableSet())
    }


fun List<ProjectPartnerBudgetInfrastructureEntity>.infrastructureEntitiesToBudgetGeneralEntries() =
    this.map { it.toBudgetGeneralCostEntry() }

fun Set<BudgetGeneralCostEntry>.toProjectPartnerBudgetInfrastructureEntity(
    partnerId: Long,
    projectPeriodEntityReferenceResolver: (Int) -> ProjectPeriodEntity
) = map { it.toProjectPartnerBudgetInfrastructureEntity(partnerId, projectPeriodEntityReferenceResolver) }

fun BudgetGeneralCostEntry.toProjectPartnerBudgetInfrastructureEntity(
    partnerId: Long,
    projectPeriodEntityReferenceResolver: (Int) -> ProjectPeriodEntity
) =
    ProjectPartnerBudgetInfrastructureEntity(
        id = id ?: 0L,
        investmentId = investmentId,
        baseProperties = BaseBudgetProperties(partnerId, numberOfUnits, rowSum!!),
        pricePerUnit = pricePerUnit,
        budgetPeriodEntities = mutableSetOf(),
        translatedValues = mutableSetOf()
    ).apply {
        translatedValues.addAll(
            description.plus(awardProcedures).plus(unitType)
                .mapTo(HashSet()) { it.language }
                .map { language ->
                    ProjectPartnerBudgetInfrastructureTranslEntity(
                        budgetTranslation = BudgetTranslation(this, language),
                        description = description.firstOrNull { it.language == language }?.translation ?: "",
                        unitType = unitType.firstOrNull { it.language == language }?.translation ?: "",
                        awardProcedures = awardProcedures.firstOrNull { it.language == language }?.translation ?: ""
                    )
                }.toMutableSet()
        )

        budgetPeriodEntities.addAll(budgetPeriods.map {
            ProjectPartnerBudgetInfrastructurePeriodEntity(
                BudgetPeriodId(
                    this,
                    projectPeriodEntityReferenceResolver.invoke(it.number)
                ),
                it.amount
            )
        }.toMutableSet())
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
    budgetPeriods = budgetPeriodEntities.map { BudgetPeriod(it.budgetPeriodId.period.id.number, it.amount) }
        .toMutableSet(),
    investmentId = investmentId,
    numberOfUnits = baseProperties.numberOfUnits,
    pricePerUnit = pricePerUnit,
    rowSum = baseProperties.rowSum
)
