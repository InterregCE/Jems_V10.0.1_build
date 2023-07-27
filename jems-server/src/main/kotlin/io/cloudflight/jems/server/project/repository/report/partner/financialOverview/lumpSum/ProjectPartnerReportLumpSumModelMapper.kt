package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.lumpSum

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportLumpSumEntity
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumBreakdownLine

fun PartnerReportLumpSumEntity.toModel() = ExpenditureLumpSumBreakdownLine(
    reportLumpSumId = id,
    lumpSumId = programmeLumpSum.id,
    name = programmeLumpSum.translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.name)
    },
    period = period,
    totalEligibleBudget = total,
    previouslyReported = previouslyReported,
    previouslyReportedParked = previouslyReportedParked,
    currentReport = current,
    currentReportReIncluded = currentReIncluded,
    totalEligibleAfterControl = totalEligibleAfterControl,
    previouslyValidated = previouslyValidated,
    previouslyPaid = previouslyPaid
)
