package io.cloudflight.jems.server.project.service.report.project.workPlan

import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageInvestment
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanFlag
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanFlag.Gray
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanFlag.Green
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanFlag.Yellow
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanInvestmentStatus.Finalized
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanStatus.Fully

fun List<ProjectReportWorkPackage>.fillInFlags() = onEach {
    it.activities.forEach { activity ->
        activity.activityStatusLabel = calculateActivityStatusLabel(activity)
    }
    it.investments.forEach { investment ->
        investment.statusLabel = calculateInvestmentStatusLabel(investment)
    }
    it.communicationStatusLabel = calculateCommunicationStatusLabel(it)
    it.specificStatusLabel = calculateWorkPackageSpecificStatusLabel(it)
    it.workPlanStatusLabel = calculateWorkPlanStatusLabel(it)
}

fun calculateActivityStatusLabel(activity: ProjectReportWorkPackageActivity): ProjectReportWorkPlanFlag? =
    when {
        (activity.status == Fully && activity.previousStatus != Fully) -> Green
        (activity.previousStatus == Fully && (activity.hasProgressChanged() || activity.haveDeliverablesChanged())) -> Yellow
        (activity.status == Fully && activity.previousStatus == Fully) -> Gray
        else -> null
    }

fun calculateInvestmentStatusLabel(investment: ProjectReportWorkPackageInvestment): ProjectReportWorkPlanFlag? =
    when {
        investment.status == Finalized && investment.previousStatus != Finalized -> Green
        investment.previousStatus == Finalized && investment.hasProgressChanged() -> Yellow
        investment.status == Finalized && investment.previousStatus == Finalized -> Gray
        else -> null
    }

fun haveDeliverablesChanged(deliverables: List<ProjectReportWorkPackageActivityDeliverable>): Boolean {
    val differences = deliverables.map {
        it.currentReport.compareTo(it.previousCurrentReport) != 0 || it.previousProgress != it.progress
    }

    return differences.contains(true)
}

fun calculateCommunicationStatusLabel(workpackage: ProjectReportWorkPackage): ProjectReportWorkPlanFlag? =
    when {
        (workpackage.communicationStatus == Fully && workpackage.previousCommunicationStatus != Fully) -> Green

        (workpackage.previousCommunicationStatus == Fully && workpackage.hasCommunicationExplanationChanged()) -> Yellow

        (workpackage.communicationStatus == Fully && workpackage.previousCommunicationStatus == Fully) -> Gray
        else -> null
    }

fun calculateWorkPackageSpecificStatusLabel(workpackage: ProjectReportWorkPackage): ProjectReportWorkPlanFlag? =
    when {
        (workpackage.specificStatus == Fully && workpackage.previousSpecificStatus != Fully) -> Green
        (workpackage.previousSpecificStatus == Fully && workpackage.hasSpecificExplanationChanged()) -> Yellow
        (workpackage.specificStatus == Fully && workpackage.previousSpecificStatus == Fully) -> Gray
        else -> null
    }

fun calculateWorkPlanStatusLabel(
    workpackage: ProjectReportWorkPackage,
): ProjectReportWorkPlanFlag? =
    when {
        (workpackage.completed
                && workpackage.previousCompleted != workpackage.completed) ->  Green

        (workpackage.hasCommunicationExplanationChanged()
                || workpackage.hasCommunicationStatusChanged()
                || workpackage.hasSpecificExplanationChanged()
                || workpackage.hasSpecificStatusChanged()
                || workpackage.haveActivitiesChanged()
                || workpackage.haveOutputsChanged()
                || workpackage.haveInvestmentsChanged()
                || workpackage.hasDescriptionChanged()) -> Yellow

        (workpackage.completed
                && workpackage.previousCompleted == workpackage.completed) -> Gray

        else -> null
    }
