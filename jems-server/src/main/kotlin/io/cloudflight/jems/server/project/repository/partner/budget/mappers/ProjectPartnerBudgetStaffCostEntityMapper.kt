package io.cloudflight.jems.server.project.repository.partner.budget.mappers

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.addTranslationEntities
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.partner.budget.BaseBudgetProperties
import io.cloudflight.jems.server.project.entity.partner.budget.BudgetPeriodId
import io.cloudflight.jems.server.project.entity.partner.budget.staff_cost.ProjectPartnerBudgetStaffCostEntity
import io.cloudflight.jems.server.project.entity.partner.budget.staff_cost.ProjectPartnerBudgetStaffCostPeriodEntity
import io.cloudflight.jems.server.project.entity.partner.budget.staff_cost.ProjectPartnerBudgetStaffCostRow
import io.cloudflight.jems.server.project.entity.partner.budget.staff_cost.ProjectPartnerBudgetStaffCostTranslEntity
import io.cloudflight.jems.server.project.service.partner.model.BudgetPeriod
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry

fun List<ProjectPartnerBudgetStaffCostRow>.toBudgetStaffCostEntryList() =
    this.groupBy { it.getId() }.map { groupedRows ->
        BudgetStaffCostEntry(
            id = groupedRows.key,
            description = groupedRows.value.extractField { it.getDescription() },
            comment = groupedRows.value.extractField { it.getComment() },
            unitType = groupedRows.value.extractField { it.getUnitType() },
            budgetPeriods = groupedRows.value.filter { it.getPeriodNumber() != null }
                .mapTo(HashSet()) { BudgetPeriod(it.getPeriodNumber()!!, it.getAmount()) },
            unitCostId = groupedRows.value.first().getUnitCostId(),
            numberOfUnits = groupedRows.value.first().getNumberOfUnits(),
            pricePerUnit = groupedRows.value.first().getPricePerUnit(),
            rowSum = groupedRows.value.first().getRowSum()
        )
    }

fun List<ProjectPartnerBudgetStaffCostEntity>.toBudgetStaffCostEntries() = this.map { it.toBudgetStaffCostEntry() }
fun ProjectPartnerBudgetStaffCostEntity.toBudgetStaffCostEntry() = BudgetStaffCostEntry(
    id = id,
    description = translatedValues.extractField { it.description },
    comment = translatedValues.extractField { it.comment },
    unitType = translatedValues.extractField { it.unitType },
    budgetPeriods = budgetPeriodEntities.map { BudgetPeriod(it.budgetPeriodId.period.id.number, it.amount) }
        .toMutableSet(),
    unitCostId = unitCostId,
    numberOfUnits = baseProperties.numberOfUnits,
    pricePerUnit = pricePerUnit,
    rowSum = baseProperties.rowSum
)

fun List<BudgetStaffCostEntry>.toProjectPartnerBudgetStaffCostEntities(
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
        unitCostId = unitCostId,
        translatedValues = mutableSetOf(),
        budgetPeriodEntities = mutableSetOf(),
        id = id ?: 0L
    ).apply {
        translatedValues.addTranslationEntities(
            { language ->
                ProjectPartnerBudgetStaffCostTranslEntity(
                    translationId = TranslationId(this, language),
                    description = description.extractTranslation(language),
                    comment = comment.extractTranslation(language),
                    unitType = unitType.extractTranslation(language),
                )
            }, arrayOf(description, comment, unitType)
        )

        budgetPeriodEntities.addAll(budgetPeriods.map {
            ProjectPartnerBudgetStaffCostPeriodEntity(
                BudgetPeriodId(this, projectPeriodEntityReferenceResolver.invoke(it.number)), it.amount
            )
        }.toMutableSet())
    }
