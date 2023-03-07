package io.cloudflight.jems.server.project.repository.report.project.workPlan

import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.programme.repository.indicator.toOutputIndicatorSummary
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageOutputEntity
import io.cloudflight.jems.server.project.repository.report.partner.toModel
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageOutput

fun List<ProjectReportWorkPackageEntity>.toModel(
    periods: Map<Int, ProjectPeriod>,
    retrieveActivities: (ProjectReportWorkPackageEntity) -> List<ProjectReportWorkPackageActivityEntity>,
    retrieveDeliverables: (ProjectReportWorkPackageActivityEntity) -> List<ProjectReportWorkPackageActivityDeliverableEntity>,
    retrieveOutputs: (ProjectReportWorkPackageEntity) -> List<ProjectReportWorkPackageOutputEntity>,
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
        outputs = retrieveOutputs.invoke(it).toOutputsModel(periods)
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
    )
}
