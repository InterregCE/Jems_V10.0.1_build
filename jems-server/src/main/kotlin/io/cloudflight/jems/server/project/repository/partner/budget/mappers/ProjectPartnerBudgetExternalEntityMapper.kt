package io.cloudflight.jems.server.project.repository.partner.budget.mappers

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.addTranslationEntities
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.partner.budget.BaseBudgetProperties
import io.cloudflight.jems.server.project.entity.partner.budget.BudgetPeriodId
import io.cloudflight.jems.server.project.entity.partner.budget.general.external.ProjectPartnerBudgetExternalEntity
import io.cloudflight.jems.server.project.entity.partner.budget.general.external.ProjectPartnerBudgetExternalPeriodEntity
import io.cloudflight.jems.server.project.entity.partner.budget.general.external.ProjectPartnerBudgetExternalTranslEntity
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry

fun List<ProjectPartnerBudgetExternalEntity>.toBudgetGeneralEntryList() =
    this.map { it.toBudgetGeneralCostEntry() }

fun List<BudgetGeneralCostEntry>.toProjectPartnerBudgetExternalEntities(
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
        unitCostId = unitCostId,
        baseProperties = BaseBudgetProperties(partnerId, numberOfUnits, rowSum!!),
        pricePerUnit = pricePerUnit,
        budgetPeriodEntities = mutableSetOf(),
        translatedValues = mutableSetOf()
    ).apply {
        translatedValues.addTranslationEntities(
            { language ->
                ProjectPartnerBudgetExternalTranslEntity(
                    translationId = TranslationId(this, language),
                    description = description.extractTranslation(language),
                    awardProcedures = awardProcedures.extractTranslation(language),
                    unitType = unitType.extractTranslation(language),
                    comments = comments.extractTranslation(language)
                )
            }, arrayOf(description, awardProcedures, unitType)
        )
        budgetPeriodEntities.addAll(budgetPeriods.map {
            ProjectPartnerBudgetExternalPeriodEntity(
                BudgetPeriodId(this, projectPeriodEntityReferenceResolver.invoke(it.number)), it.amount
            )
        }.toMutableSet())
    }
