package io.cloudflight.jems.server.project.repository.report.identification

import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentificationTargetGroup

fun ProjectPartnerReportIdentificationEntity.toModel(
    targetGroups: List<ProjectPartnerReportIdentificationTargetGroupEntity>,
) = ProjectPartnerReportIdentification(
    startDate = startDate,
    endDate = endDate,
    period = periodNumber,
    summary = translatedValues.extractField { it.summary },
    problemsAndDeviations = translatedValues.extractField { it.problemsAndDeviations },
    targetGroups = targetGroups.toModel(),
)

fun List<ProjectPartnerReportIdentificationTargetGroupEntity>.toModel() = map {
    ProjectPartnerReportIdentificationTargetGroup(
        type = it.type,
        sortNumber = it.sortNumber,
        specification = it.translatedValues.extractField { it.specification },
        description = it.translatedValues.extractField { it.description },
    )
}
