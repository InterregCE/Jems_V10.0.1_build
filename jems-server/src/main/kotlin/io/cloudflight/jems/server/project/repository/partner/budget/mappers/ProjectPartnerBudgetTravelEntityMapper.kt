package io.cloudflight.jems.server.project.repository.partner.budget.mappers

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.addTranslationEntities
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.partner.budget.BaseBudgetProperties
import io.cloudflight.jems.server.project.entity.partner.budget.BudgetPeriodId
import io.cloudflight.jems.server.project.entity.partner.budget.travel.ProjectPartnerBudgetTravelCostRow
import io.cloudflight.jems.server.project.entity.partner.budget.travel.ProjectPartnerBudgetTravelEntity
import io.cloudflight.jems.server.project.entity.partner.budget.travel.ProjectPartnerBudgetTravelPeriodEntity
import io.cloudflight.jems.server.project.entity.partner.budget.travel.ProjectPartnerBudgetTravelTranslEntity
import io.cloudflight.jems.server.project.service.partner.model.BudgetPeriod
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry

fun List<ProjectPartnerBudgetTravelCostRow>.toBudgetTravelCostEntryList() =
    this.groupBy { it.getId() }.map { groupedRows ->
        BudgetTravelAndAccommodationCostEntry(
            id = groupedRows.key,
            description = groupedRows.value.extractField { it.getDescription() },
            unitType = groupedRows.value.extractField { it.getUnitType() },
            budgetPeriods = groupedRows.value.filter { it.getPeriodNumber() != null }
                .mapTo(HashSet()) { BudgetPeriod(it.getPeriodNumber()!!, it.getAmount()) },
            unitCostId = groupedRows.value.first().getUnitCostId(),
            numberOfUnits = groupedRows.value.first().getNumberOfUnits(),
            pricePerUnit = groupedRows.value.first().getPricePerUnit(),
            rowSum = groupedRows.value.first().getRowSum()
        )
    }

fun List<ProjectPartnerBudgetTravelEntity>.toBudgetTravelAndAccommodationCostEntries() =
    this.map { it.toBudgetTravelAndAccommodationCostEntry() }

fun ProjectPartnerBudgetTravelEntity.toBudgetTravelAndAccommodationCostEntry() = BudgetTravelAndAccommodationCostEntry(
    id = id,
    description = translatedValues.extractField { it.description },
    unitType = translatedValues.extractField { it.unitType },
    budgetPeriods = budgetPeriodEntities.map { BudgetPeriod(it.budgetPeriodId.period.id.number, it.amount) }
        .toMutableSet(),
    unitCostId = unitCostId,
    numberOfUnits = baseProperties.numberOfUnits,
    pricePerUnit = pricePerUnit,
    rowSum = baseProperties.rowSum
)

fun List<BudgetTravelAndAccommodationCostEntry>.toProjectPartnerBudgetTravelEntities(
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
        unitCostId = unitCostId,
        translatedValues = mutableSetOf(),
        budgetPeriodEntities = mutableSetOf(),
        id = id ?: 0L
    ).apply {
        translatedValues.addTranslationEntities(
            { language ->
                ProjectPartnerBudgetTravelTranslEntity(
                    translationId = TranslationId(this, language),
                    description = description.extractTranslation(language),
                    unitType = unitType.extractTranslation(language),
                )
            }, arrayOf(description, unitType)
        )

        budgetPeriodEntities.addAll(budgetPeriods.map {
            ProjectPartnerBudgetTravelPeriodEntity(
                BudgetPeriodId(this, projectPeriodEntityReferenceResolver.invoke(it.number)), it.amount
            )
        }.toMutableSet())
    }
