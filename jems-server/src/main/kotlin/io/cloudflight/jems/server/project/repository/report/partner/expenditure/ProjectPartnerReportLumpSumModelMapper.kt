package io.cloudflight.jems.server.project.repository.report.partner.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportLumpSumEntity
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum

fun List<PartnerReportLumpSumEntity>.toModel() = map {
    ProjectPartnerReportLumpSum(
        id = it.id,
        lumpSumProgrammeId = it.programmeLumpSum.id,
        fastTrack = it.programmeLumpSum.isFastTrack,
        period = it.period,
        cost = it.total,
        name = it.programmeLumpSum.translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.name) },
    )
}
