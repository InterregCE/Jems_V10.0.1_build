package io.cloudflight.jems.server.project.repository.report.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportUnitCostEntity
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.model.expenditure.ReportBudgetCategory


fun List<PartnerReportUnitCostEntity>.toModel() = map {
    ProjectPartnerReportUnitCost(
        id = it.id,
        unitCostProgrammeId = it.programmeUnitCost.id,
        costPerUnit = it.programmeUnitCost.costPerUnit,
        name = it.programmeUnitCost.translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.name) },
        total = it.totalCost,
        numberOfUnits = it.numberOfUnits,
        costPerUnitForeignCurrency = it.programmeUnitCost.costPerUnitForeignCurrency,
        foreignCurrencyCode = it.programmeUnitCost.foreignCurrencyCode,
        category = if (it.programmeUnitCost.isOneCostCategory) ReportBudgetCategory.valueOf(it.programmeUnitCost.categories.first().category.name)
        else ReportBudgetCategory.Multiple
    )
}
