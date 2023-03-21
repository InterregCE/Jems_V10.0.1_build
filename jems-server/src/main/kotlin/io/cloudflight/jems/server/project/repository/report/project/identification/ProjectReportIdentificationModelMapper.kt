package io.cloudflight.jems.server.project.repository.report.project.identification

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportSpendingProfileEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportSpendingProfileId
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportSpendingProfileReportedValues

fun List<ProjectReportIdentificationTargetGroupEntity>.toModel() = map {
    ProjectReportIdentificationTargetGroup(
        type = it.type,
        sortNumber = it.sortNumber,
        description = it.translatedValues.extractField { it.description },
    )
}

fun List<ProjectReportSpendingProfileEntity>.toReportedValuesModel() = map {
    ProjectReportSpendingProfileReportedValues(
        partnerId = it.id.partnerId,
        previouslyReported = it.previouslyReported,
        currentlyReported = it.currentlyReported
    )
}

fun toProjectReportIdentification(
    projectReportEntity: ProjectReportEntity,
    targetGroupEntities: List<ProjectReportIdentificationTargetGroupEntity>
): ProjectReportIdentification {
    return ProjectReportIdentification(
        targetGroups = targetGroupEntities.toModel(),
        highlights = projectReportEntity.translatedValues.mapTo(HashSet()) {
            InputTranslation(
                it.language(),
                it.highlights
            )
        },
        deviations = projectReportEntity.translatedValues.mapTo(HashSet()) {
            InputTranslation(
                it.language(),
                it.deviations
            )
        },
        partnerProblems = projectReportEntity.translatedValues.mapTo(HashSet()) {
            InputTranslation(
                it.language(),
                it.partnerProblems
            )
        },
        spendingProfiles = listOf()
    )
}
