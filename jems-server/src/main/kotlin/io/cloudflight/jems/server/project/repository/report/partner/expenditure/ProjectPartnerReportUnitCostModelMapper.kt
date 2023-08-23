package io.cloudflight.jems.server.project.repository.report.partner.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportUnitCostEntity
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory


fun List<PartnerReportUnitCostEntity>.toModel() = map { it.toModel() }

fun PartnerReportUnitCostEntity.toModel() =
    ProjectPartnerReportUnitCost(
        id = id,
        unitCostProgrammeId = programmeUnitCost.id,
        projectDefined = programmeUnitCost.projectId != null,
        costPerUnit = programmeUnitCost.costPerUnit,
        name = programmeUnitCost.translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.name) },
        total = total,
        numberOfUnits = numberOfUnits,
        costPerUnitForeignCurrency = programmeUnitCost.costPerUnitForeignCurrency,
        foreignCurrencyCode = programmeUnitCost.foreignCurrencyCode,
        category = if (programmeUnitCost.isOneCostCategory) ReportBudgetCategory.valueOf(programmeUnitCost.categories.first().category.name)
        else ReportBudgetCategory.Multiple
    )
