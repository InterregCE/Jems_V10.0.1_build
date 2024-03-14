package io.cloudflight.jems.server.project.service.report.model.project.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.project.workPlan.haveDeliverablesChanged

data class ProjectReportWorkPackage(
    val id: Long,
    val number: Int,
    val deactivated: Boolean,

    val specificObjective: Set<InputTranslation>,
    val previousSpecificStatus: ProjectReportWorkPlanStatus?,
    val specificStatus: ProjectReportWorkPlanStatus?,
    val previousSpecificExplanation: Set<InputTranslation>,
    val specificExplanation: Set<InputTranslation>,

    val communicationObjective: Set<InputTranslation>,
    val previousCommunicationStatus: ProjectReportWorkPlanStatus?,
    val communicationStatus: ProjectReportWorkPlanStatus?,
    val previousCommunicationExplanation: Set<InputTranslation>,
    val communicationExplanation: Set<InputTranslation>,

    val previousCompleted: Boolean,
    val completed: Boolean,
    val previousDescription: Set<InputTranslation>,
    val description: Set<InputTranslation>,
    val activities: List<ProjectReportWorkPackageActivity>,
    val outputs: List<ProjectReportWorkPackageOutput>,
    val investments: List<ProjectReportWorkPackageInvestment>,

    var specificStatusLabel: ProjectReportWorkPlanFlag? = null,
    var communicationStatusLabel: ProjectReportWorkPlanFlag? = null,
    var workPlanStatusLabel: ProjectReportWorkPlanFlag? = null,
){
    fun hasSpecificExplanationChanged() = specificExplanation != previousSpecificExplanation

    fun hasSpecificStatusChanged() = specificStatus != previousSpecificStatus

    fun hasCommunicationExplanationChanged() = communicationExplanation != previousCommunicationExplanation

    fun hasCommunicationStatusChanged() = communicationStatus != previousCommunicationStatus

    fun haveActivitiesChanged(): Boolean = activities.any {
        it.previousProgress != it.progress
                || it.previousStatus != it.status
                || haveDeliverablesChanged(it.deliverables)
    }

    fun haveOutputsChanged(): Boolean =  outputs.any {
        it.currentReport.compareTo(it.previousCurrentReport) != 0 || it.previousProgress != it.progress
    }

    fun haveInvestmentsChanged(): Boolean = investments.any {
        it.previousProgress != it.progress
                || it.previousStatus != it.status
    }

    fun hasDescriptionChanged() = description != previousDescription
}
