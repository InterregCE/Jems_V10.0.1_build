package io.cloudflight.jems.server.project.repository.report.partner.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportLumpSumEntity
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum

fun List<PartnerReportLumpSumEntity>.toModel() = map { it.toModel() }

fun PartnerReportLumpSumEntity.toModel() =
    ProjectPartnerReportLumpSum(
        id = id,
        lumpSumProgrammeId = programmeLumpSum.id,
        fastTrack = programmeLumpSum.isFastTrack,
        orderNr = orderNr,
        period = period,
        cost = total,
        name = programmeLumpSum.translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.name) },
    )
