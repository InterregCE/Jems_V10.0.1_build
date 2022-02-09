package io.cloudflight.jems.server.project.repository.partner.budget.mappers

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.addTranslationEntities
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.partner.budget.BaseBudgetProperties
import io.cloudflight.jems.server.project.entity.partner.budget.BudgetPeriodId
import io.cloudflight.jems.server.project.entity.partner.budget.spf.ProjectPartnerBudgetSpfCostEntity
import io.cloudflight.jems.server.project.entity.partner.budget.spf.ProjectPartnerBudgetSpfCostPeriodEntity
import io.cloudflight.jems.server.project.entity.partner.budget.spf.ProjectPartnerBudgetSpfCostRow
import io.cloudflight.jems.server.project.entity.partner.budget.spf.ProjectPartnerBudgetSpfCostTranslEntity
import io.cloudflight.jems.server.project.service.partner.model.BudgetPeriod
import io.cloudflight.jems.server.project.service.partner.model.BudgetSpfCostEntry

fun List<ProjectPartnerBudgetSpfCostRow>.toBudgetSpfCostEntryList() =
    this.groupBy { it.getId() }.map { groupedRows ->
        BudgetSpfCostEntry(
            id = groupedRows.key,
            description = groupedRows.value.extractField { it.getDescription() },
            comments = groupedRows.value.extractField { it.getComments() },
            unitType = groupedRows.value.extractField { it.getUnitType() },
            budgetPeriods = groupedRows.value.filter { it.getPeriodNumber() != null }
                .mapTo(HashSet()) { BudgetPeriod(it.getPeriodNumber()!!, it.getAmount()) },
            unitCostId = groupedRows.value.first().getUnitCostId(),
            numberOfUnits = groupedRows.value.first().getNumberOfUnits(),
            pricePerUnit = groupedRows.value.first().getPricePerUnit(),
            rowSum = groupedRows.value.first().getRowSum()
        )
    }

fun List<ProjectPartnerBudgetSpfCostEntity>.toBudgetSpfCostEntries() =
    this.map { it.toBudgetSpfCostEntry() }

fun ProjectPartnerBudgetSpfCostEntity.toBudgetSpfCostEntry() = BudgetSpfCostEntry(
    id = id,
    description = translatedValues.extractField { it.description },
    unitType = translatedValues.extractField { it.unitType },
    budgetPeriods = budgetPeriodEntities.map { BudgetPeriod(it.budgetPeriodId.period.id.number, it.amount) }
    .toMutableSet(),
    unitCostId = unitCostId,
    numberOfUnits = baseProperties.numberOfUnits,
    pricePerUnit = pricePerUnit,
    rowSum = baseProperties.rowSum,
    comments = translatedValues.extractField { it.comments }
)

fun List<BudgetSpfCostEntry>.toProjectPartnerBudgetSpfCostEntities(
    partnerId: Long,
    projectPeriodEntityReferenceResolver: (Int) -> ProjectPeriodEntity
) = map { it.toProjectPartnerBudgetSpfCostEntity(partnerId, projectPeriodEntityReferenceResolver) }

fun BudgetSpfCostEntry.toProjectPartnerBudgetSpfCostEntity(
    partnerId: Long,
    projectPeriodEntityReferenceResolver: (Int) -> ProjectPeriodEntity
) =
    ProjectPartnerBudgetSpfCostEntity(
        baseProperties = BaseBudgetProperties(partnerId, numberOfUnits, rowSum!!),
        pricePerUnit = pricePerUnit,
        unitCostId = unitCostId,
        translatedValues = mutableSetOf(),
        budgetPeriodEntities = mutableSetOf(),
        id = id ?: 0L
    ).apply {
        translatedValues.addTranslationEntities(
            { language ->
                ProjectPartnerBudgetSpfCostTranslEntity(
                    translationId = TranslationId(this, language),
                    description = description.extractTranslation(language),
                    unitType = unitType.extractTranslation(language),
                    comments = comments.extractTranslation(language)
                )
            }, arrayOf(description, unitType, comments)
        )

        budgetPeriodEntities.addAll(budgetPeriods.map {
            ProjectPartnerBudgetSpfCostPeriodEntity(
                BudgetPeriodId(this, projectPeriodEntityReferenceResolver.invoke(it.number)), it.amount
            )
        }.toMutableSet())
    }
