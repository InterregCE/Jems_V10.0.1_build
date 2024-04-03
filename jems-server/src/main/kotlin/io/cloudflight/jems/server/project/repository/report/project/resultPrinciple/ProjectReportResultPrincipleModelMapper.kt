package io.cloudflight.jems.server.project.repository.report.project.resultPrinciple

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorEntity
import io.cloudflight.jems.server.programme.repository.indicator.getMeasurementUnit
import io.cloudflight.jems.server.programme.repository.indicator.getName
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.resultPrinciple.ProjectReportHorizontalPrincipleEntity
import io.cloudflight.jems.server.project.entity.report.project.resultPrinciple.ProjectReportProjectResultEntity
import io.cloudflight.jems.server.project.repository.report.partner.toModel
import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportResultCreate
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportProjectResult
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrinciple
import java.math.BigDecimal

fun toResultPrincipleModel(
    projectResultEntities: List<ProjectReportProjectResultEntity>,
    horizontalPrincipleEntity: ProjectReportHorizontalPrincipleEntity,
    periodResolver: (Int) -> ProjectPeriod?
) = ProjectReportResultPrinciple(
    projectResults = projectResultEntities.toModel(periodResolver = periodResolver),
    horizontalPrinciples = ProjectHorizontalPrinciples(
        horizontalPrincipleEntity.sustainableDevelopmentCriteriaEffect,
        horizontalPrincipleEntity.equalOpportunitiesEffect,
        horizontalPrincipleEntity.sexualEqualityEffect
    ),
    sustainableDevelopmentDescription = horizontalPrincipleEntity.translatedValues.extractField { it.sustainableDevelopmentDescription },
    equalOpportunitiesDescription = horizontalPrincipleEntity.translatedValues.extractField { it.equalOpportunitiesDescription },
    sexualEqualityDescription = horizontalPrincipleEntity.translatedValues.extractField { it.sexualEqualityDescription },
)

fun List<ProjectReportProjectResultEntity>.toModel(
    periodResolver: (Int) -> ProjectPeriod?
) = map {
    ProjectReportProjectResult(
        resultNumber = it.resultNumber,
        deactivated = it.deactivated,
        programmeResultIndicatorId = it.programmeResultIndicatorEntity?.id,
        programmeResultIndicatorIdentifier = it.programmeResultIndicatorEntity?.identifier,
        programmeResultIndicatorName = it.programmeResultIndicatorEntity?.getName() ?: mutableSetOf(),
        baseline = it.baseline,
        targetValue = it.targetValue,
        currentReport = it.currentReport,
        previouslyReported = it.previouslyReported,
        periodDetail = it.periodNumber?.let { number -> periodResolver.invoke(number) },
        description = it.getDescription(),
        measurementUnit = it.programmeResultIndicatorEntity?.getMeasurementUnit() ?: emptySet(),
        attachment = it.attachment?.toModel(),
    )
}

fun List<ProjectReportResultCreate>.toIndexedEntity(
    projectReport: ProjectReportEntity,
    indicatorEntityResolver: (Long?) -> ResultIndicatorEntity?,
) = map {
    ProjectReportProjectResultEntity(
        projectReport = projectReport,
        resultNumber = it.resultNumber,
        deactivated = it.deactivated,
        periodNumber = it.periodNumber,
        programmeResultIndicatorEntity = indicatorEntityResolver.invoke(it.programmeResultIndicatorId),
        baseline = it.baseline,
        targetValue = it.targetValue,
        currentReport = BigDecimal.ZERO,
        previouslyReported = it.previouslyReported,
        attachment = null
    )
}

private fun ProjectReportProjectResultEntity.getDescription() = translatedValues.mapTo(HashSet()) {
    InputTranslation(it.translationId.language, it.description)
}
