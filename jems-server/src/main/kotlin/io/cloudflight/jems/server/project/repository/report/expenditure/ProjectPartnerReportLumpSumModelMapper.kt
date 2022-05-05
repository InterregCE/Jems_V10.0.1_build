package io.cloudflight.jems.server.project.repository.report.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportLumpSumEntity
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportLumpSum

fun List<PartnerReportLumpSumEntity>.toModel() = map {
    ProjectPartnerReportLumpSum(
        id = it.id,
        lumpSumProgrammeId = it.programmeLumpSum.id,
        period = it.period,
        cost = it.cost,
        name = it.programmeLumpSum.translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.name) },
    )
}
