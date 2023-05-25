package io.cloudflight.jems.server.project.service.report.project.workPlan

import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageInvestment
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanFlag
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanStatus


fun List<ProjectReportWorkPackage>.fillInFlags() = onEach {
    it.activities.forEach { activity ->
        activity.activityStatusLabel = hasActivityChanged(activity)
    }
    val communicationChanged = hasCommunicationChanged(it)
    val specificChanged = hasSpecificChanged(it)
    it.communicationStatusLabel = communicationChanged
    it.specificStatusLabel = specificChanged
    it.workPlanStatusLabel = hasWorkPlanChanged(
        it,
        it.activities,
        it.outputs,
        it.investments
    )
}
fun hasActivityChanged(activity: ProjectReportWorkPackageActivity): ProjectReportWorkPlanFlag? {
    if (activity.status == ProjectReportWorkPlanStatus.Fully
        && activity.previousStatus != ProjectReportWorkPlanStatus.Fully) {
        return ProjectReportWorkPlanFlag.Green
    }
    if (activity.previousStatus == ProjectReportWorkPlanStatus.Fully
        && (activity.progress != activity.previousProgress
                || haveDeliverablesChanged(activity.deliverables))) {
        return ProjectReportWorkPlanFlag.Yellow
    }
    if (activity.status == ProjectReportWorkPlanStatus.Fully
        && activity.previousStatus == ProjectReportWorkPlanStatus.Fully) {
        return ProjectReportWorkPlanFlag.Gray
    }
    return null
}

fun haveDeliverablesChanged(deliverables: List<ProjectReportWorkPackageActivityDeliverable>): Boolean {
    val differences = deliverables.map {
        it.currentReport.compareTo(it.previousCurrentReport) != 0 || it.previousProgress != it.progress
    }

    return differences.contains(true)
}

fun hasCommunicationChanged(workpackage: ProjectReportWorkPackage): ProjectReportWorkPlanFlag? {
    if (workpackage.communicationStatus == ProjectReportWorkPlanStatus.Fully
        && workpackage.previousCommunicationStatus != ProjectReportWorkPlanStatus.Fully) {
        return ProjectReportWorkPlanFlag.Green
    }
    if (workpackage.previousCommunicationStatus == ProjectReportWorkPlanStatus.Fully
        && workpackage.communicationExplanation != workpackage.previousCommunicationExplanation) {
        return ProjectReportWorkPlanFlag.Yellow
    }
    if (workpackage.communicationStatus == ProjectReportWorkPlanStatus.Fully
        && workpackage.previousCommunicationStatus == ProjectReportWorkPlanStatus.Fully) {
        return ProjectReportWorkPlanFlag.Gray
    }
    return null
}

fun hasSpecificChanged(workpackage: ProjectReportWorkPackage): ProjectReportWorkPlanFlag? {
    if (workpackage.specificStatus == ProjectReportWorkPlanStatus.Fully
        && workpackage.previousSpecificStatus != ProjectReportWorkPlanStatus.Fully) {
        return ProjectReportWorkPlanFlag.Green
    }
    if (workpackage.previousSpecificStatus == ProjectReportWorkPlanStatus.Fully
        && workpackage.specificExplanation != workpackage.previousSpecificExplanation) {
        return ProjectReportWorkPlanFlag.Yellow
    }
    if (workpackage.specificStatus == ProjectReportWorkPlanStatus.Fully
        && workpackage.previousSpecificStatus == ProjectReportWorkPlanStatus.Fully) {
        return ProjectReportWorkPlanFlag.Gray
    }
    return null
}

fun hasWorkPlanChanged(
    workpackage: ProjectReportWorkPackage,
    activities: List<ProjectReportWorkPackageActivity>,
    outputs: List<ProjectReportWorkPackageOutput>,
    investments: List<ProjectReportWorkPackageInvestment>
): ProjectReportWorkPlanFlag? {
    if (workpackage.completed
        && workpackage.previousCompleted != workpackage.completed) {
        return ProjectReportWorkPlanFlag.Green
    }
    if (workpackage.communicationExplanation != workpackage.previousCommunicationExplanation
        || workpackage.communicationStatus != workpackage.previousCommunicationStatus
        || workpackage.specificExplanation != workpackage.previousSpecificExplanation
        || workpackage.specificStatus != workpackage.previousSpecificStatus
        || haveActivitiesChanged(activities)
        || haveOutputsChanged(outputs)
        || haveInvestmentsChanged(investments)
        || workpackage.description != workpackage.previousDescription) {
        return ProjectReportWorkPlanFlag.Yellow
    }
    if (workpackage.completed
        && workpackage.previousCompleted == workpackage.completed) {
        return ProjectReportWorkPlanFlag.Gray
    }
    return null
}

fun haveOutputsChanged(outputs: List<ProjectReportWorkPackageOutput>): Boolean {
    val differences = outputs.map {
        it.currentReport.compareTo(it.previousCurrentReport) != 0 || it.previousProgress != it.progress
    }

    return differences.contains(true)
}

fun haveInvestmentsChanged(investments: List<ProjectReportWorkPackageInvestment>): Boolean {
    val differences = investments.map {
        it.previousProgress != it.progress
    }

    return differences.contains(true)
}

fun haveActivitiesChanged(activities: List<ProjectReportWorkPackageActivity>): Boolean {
    val differences = activities.map {
        it.previousProgress != it.progress
                || it.previousStatus != it.status
                || haveDeliverablesChanged(it.deliverables)
    }

    return differences.contains(true)
}
