package io.cloudflight.jems.server.project.repository.partner.budget.mappers

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.addTranslationEntities
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.partner.budget.BaseBudgetProperties
import io.cloudflight.jems.server.project.entity.partner.budget.BudgetPeriodId
import io.cloudflight.jems.server.project.entity.partner.budget.general.infrastructure.ProjectPartnerBudgetInfrastructureEntity
import io.cloudflight.jems.server.project.entity.partner.budget.general.infrastructure.ProjectPartnerBudgetInfrastructurePeriodEntity
import io.cloudflight.jems.server.project.entity.partner.budget.general.infrastructure.ProjectPartnerBudgetInfrastructureTranslEntity
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry


fun List<ProjectPartnerBudgetInfrastructureEntity>.toBudgetGeneralEntryList() =
    this.map { it.toBudgetGeneralCostEntry() }

fun List<BudgetGeneralCostEntry>.toProjectPartnerBudgetInfrastructureEntity(
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
        unitCostId = unitCostId,
        baseProperties = BaseBudgetProperties(partnerId, numberOfUnits, rowSum!!),
        pricePerUnit = pricePerUnit,
        budgetPeriodEntities = mutableSetOf(),
        translatedValues = mutableSetOf()
    ).apply {
        translatedValues.addTranslationEntities(
            { language ->
                ProjectPartnerBudgetInfrastructureTranslEntity(
                    translationId = TranslationId(this, language),
                    description = description.extractTranslation(language),
                    awardProcedures = awardProcedures.extractTranslation(language),
                    unitType = unitType.extractTranslation(language),
                    comments = comments.extractTranslation(language)
                )
            }, arrayOf(description, awardProcedures, unitType, comments)
        )

        budgetPeriodEntities.addAll(budgetPeriods.map {
            ProjectPartnerBudgetInfrastructurePeriodEntity(
                BudgetPeriodId(this, projectPeriodEntityReferenceResolver.invoke(it.number)), it.amount
            )
        }.toMutableSet())
    }
