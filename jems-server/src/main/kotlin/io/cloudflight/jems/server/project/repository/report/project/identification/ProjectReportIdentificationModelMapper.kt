package io.cloudflight.jems.server.project.repository.report.project.identification

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportSpendingProfileEntity
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportSpendingProfile
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportSpendingProfileReportedValues
import io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportIdentification.GetProjectReportIdentification.Companion.emptySpendingProfile

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
        currentlyReported = it.currentlyReported,
        partnerTotalEligibleBudget = it.partnerTotalEligibleBudget
    )
}

fun ProjectReportEntity.toProjectReportIdentification(
    targetGroupEntities: List<ProjectReportIdentificationTargetGroupEntity>,
    spendingProfilePerPartner: ProjectReportSpendingProfile?,
): ProjectReportIdentification {
    return ProjectReportIdentification(
        targetGroups = targetGroupEntities.toModel(),
        highlights = translatedValues.mapTo(HashSet()) {
            InputTranslation(
                it.language(),
                it.highlights
            )
        },
        deviations = translatedValues.mapTo(HashSet()) {
            InputTranslation(
                it.language(),
                it.deviations
            )
        },
        partnerProblems = translatedValues.mapTo(HashSet()) {
            InputTranslation(
                it.language(),
                it.partnerProblems
            )
        },
        spendingProfilePerPartner = spendingProfilePerPartner,
    )
}
