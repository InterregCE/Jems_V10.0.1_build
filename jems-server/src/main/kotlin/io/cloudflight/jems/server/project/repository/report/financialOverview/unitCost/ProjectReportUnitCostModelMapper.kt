package io.cloudflight.jems.server.project.repository.report.financialOverview.unitCost

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportUnitCostEntity
import io.cloudflight.jems.server.project.service.report.model.financialOverview.unitCost.ExpenditureUnitCostBreakdownLine

fun PartnerReportUnitCostEntity.toModel() = ExpenditureUnitCostBreakdownLine(
    reportUnitCostId = id,
    unitCostId = programmeUnitCost.id,
    name = programmeUnitCost.translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.name)
    },
    totalEligibleBudget = total,
    previouslyReported = previouslyReported,
    currentReport = current,
)
