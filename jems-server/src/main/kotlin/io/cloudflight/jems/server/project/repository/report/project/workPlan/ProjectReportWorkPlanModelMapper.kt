package io.cloudflight.jems.server.project.repository.report.project.workPlan

import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorEntity
import io.cloudflight.jems.server.programme.repository.indicator.getMeasurementUnit
import io.cloudflight.jems.server.programme.repository.indicator.getName
import io.cloudflight.jems.server.programme.repository.indicator.toOutputIndicatorSummary
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageInvestmentEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageOutputEntity
import io.cloudflight.jems.server.project.repository.report.partner.toModel
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputIndicatorOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputLineOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportResultIndicatorOverview
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageInvestment
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageOutput
import java.math.BigDecimal

fun List<ProjectReportWorkPackageEntity>.toModel(
    periods: Map<Int, ProjectPeriod>,
    retrieveActivities: (ProjectReportWorkPackageEntity) -> List<ProjectReportWorkPackageActivityEntity>,
    retrieveDeliverables: (ProjectReportWorkPackageActivityEntity) -> List<ProjectReportWorkPackageActivityDeliverableEntity>,
    retrieveOutputs: (ProjectReportWorkPackageEntity) -> List<ProjectReportWorkPackageOutputEntity>,
    retrieveInvestments: (ProjectReportWorkPackageEntity) -> List<ProjectReportWorkPackageInvestmentEntity>,
) = map {
    ProjectReportWorkPackage(
        id = it.id,
        number = it.number,
        deactivated = it.deactivated,
        specificObjective = it.translatedValues.extractField { it.specificObjective },
        specificStatus = it.specificStatus,
        specificExplanation = it.translatedValues.extractField { it.specificExplanation },
        communicationObjective = it.translatedValues.extractField { it.communicationObjective },
        communicationStatus = it.communicationStatus,
        communicationExplanation = it.translatedValues.extractField { it.communicationExplanation },
        completed = it.completed,
        description = it.translatedValues.extractField { it.description },
        activities = retrieveActivities.invoke(it).toActivitiesModel(periods, retrieveDeliverables),
        outputs = retrieveOutputs.invoke(it).toOutputsModel(periods),
        investments = retrieveInvestments.invoke(it).toInvestmentsModel(periods),
        previousSpecificStatus = it.previousSpecificStatus,
        previousSpecificExplanation = it.translatedValues.extractField { it.previousSpecificExplanation },
        previousCommunicationStatus = it.previousCommunicationStatus,
        previousCompleted = it.previousCompleted,
        previousCommunicationExplanation = it.translatedValues.extractField { it.previousCommunicationExplanation },
        previousDescription = it.translatedValues.extractField { it.previousDescription },
    )
}

fun List<ProjectReportWorkPackageActivityEntity>.toActivitiesModel(
    periods: Map<Int, ProjectPeriod>,
    retrieveDeliverables: (ProjectReportWorkPackageActivityEntity) -> List<ProjectReportWorkPackageActivityDeliverableEntity>,
) = map {
    ProjectReportWorkPackageActivity(
        id = it.id,
        number = it.number,
        title = it.translatedValues.extractField { it.title },
        deactivated = it.deactivated,
        startPeriod = it.startPeriodNumber?.let { periods[it] },
        endPeriod = it.endPeriodNumber?.let { periods[it] },
        status = it.status,
        progress = it.translatedValues.extractField { it.progress },
        attachment = it.attachment?.toModel(),
        deliverables = retrieveDeliverables.invoke(it).toDeliverablesModel(periods),
        previousStatus = it.previousStatus,
        previousProgress = it.translatedValues.extractField { it.previousProgress }
    )
}

fun List<ProjectReportWorkPackageActivityDeliverableEntity>.toDeliverablesModel(
    periods: Map<Int, ProjectPeriod>,
) = map {
    ProjectReportWorkPackageActivityDeliverable(
        id = it.id,
        number = it.number,
        title = it.translatedValues.extractField { it.title },
        deactivated = it.deactivated,
        period = it.periodNumber?.let { periods[it] },
        previouslyReported = it.previouslyReported,
        currentReport = it.currentReport,
        progress = it.translatedValues.extractField { it.progress },
        attachment = it.attachment?.toModel(),
        previousCurrentReport = it.previousCurrentReport,
        previousProgress = it.translatedValues.extractField { it.previousProgress }
    )
}

fun List<ProjectReportWorkPackageOutputEntity>.toOutputsModel(
    periods: Map<Int, ProjectPeriod>,
) = map {
    ProjectReportWorkPackageOutput(
        id = it.id,
        number = it.number,
        title = it.translatedValues.extractField { it.title },
        deactivated = it.deactivated,
        outputIndicator = it.programmeOutputIndicator?.toOutputIndicatorSummary(),
        period = it.periodNumber?.let { periods[it] },
        targetValue = it.targetValue,
        currentReport = it.currentReport,
        previouslyReported = it.previouslyReported,
        progress = it.translatedValues.extractField { it.progress },
        attachment = it.attachment?.toModel(),
        previousProgress = it.translatedValues.extractField { it.previousProgress },
        previousCurrentReport = it.previousCurrentReport
    )
}

fun List<ProjectReportWorkPackageInvestmentEntity>.toInvestmentsModel(
    periods: Map<Int, ProjectPeriod>,
) = map {
    ProjectReportWorkPackageInvestment(
        id = it.id,
        number = it.number,
        title = it.translatedValues.extractField { it.title },
        deactivated = it.deactivated,
        period = it.expectedDeliveryPeriod?.let { periods[it] },
        progress = it.translatedValues.extractField { it.progress },
        nutsRegion3 = it.address?.nutsRegion3,
        previousProgress = it.translatedValues.extractField { it.previousProgress },
        status = it.status,
        previousStatus = it.previousStatus
    )
}

fun List<ProjectReportWorkPackageOutputEntity>.toOverviewModel() = map {
    ProjectReportOutputLineOverview(
        number = it.number,
        workPackageNumber = it.workPackageEntity.number,
        name = it.translatedValues.extractField { it.title },
        measurementUnit = it.programmeOutputIndicator?.getMeasurementUnit() ?: emptySet(),
        deactivated = it.deactivated,
        outputIndicator = it.programmeOutputIndicator?.toModel(),
        targetValue = it.targetValue,
        currentReport = it.currentReport,
        previouslyReported = it.previouslyReported,
    )
}

fun OutputIndicatorEntity.toModel() = ProjectReportOutputIndicatorOverview(
    id = id,
    identifier = identifier,
    name = getName(),
    measurementUnit = getMeasurementUnit(),
    resultIndicator = resultIndicatorEntity?.toModel(),
    targetValue = BigDecimal.ZERO,
    previouslyReported = BigDecimal.ZERO,
    currentReport = BigDecimal.ZERO,
)

fun ResultIndicatorEntity.toModel() = ProjectReportResultIndicatorOverview(
    id = id,
    identifier = identifier,
    name = getName(),
    measurementUnit = getMeasurementUnit(),
    baselineIndicator = baseline ?: BigDecimal.ZERO,
    baselines = emptyList(),
    targetValue = BigDecimal.ZERO,
    previouslyReported = BigDecimal.ZERO,
    currentReport = BigDecimal.ZERO,
)
