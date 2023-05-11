package io.cloudflight.jems.server.project.service.report.project.workPlan.updateProjectReportWorkPlan

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivityDeliverableUpdate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivityUpdate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageInvestment
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageInvestmentUpdate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageOnlyUpdate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageOutputUpdate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageUpdate
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.workPlan.ProjectReportWorkPlanPersistence
import io.cloudflight.jems.server.project.service.report.project.workPlan.fillInFlags
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectReportWorkPlan(
    private val reportPersistence: ProjectReportPersistence,
    private val reportWorkPlanPersistence: ProjectReportWorkPlanPersistence,
    private val generalValidator: GeneralValidatorService,
) : UpdateProjectReportWorkPlanInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(UpdateProjectReportWorkPlanException::class)
    override fun update(
        projectId: Long,
        reportId: Long,
        workPlan: List<ProjectReportWorkPackageUpdate>,
    ): List<ProjectReportWorkPackage> {
        validateReportNotClosed(status = reportPersistence.getReportById(projectId, reportId = reportId).status)
        validateInputs(workPlan = workPlan)

        val existingWpsById = reportWorkPlanPersistence.getReportWorkPlanById(projectId = projectId, reportId = reportId)
            .associateBy { it.id }

        workPlan.associateBy { it.id }.forEach { (wpId, wpNew) ->
            val existingWp = existingWpsById.getByIdOrThrow(wpId)

            if (thereAreChanges(wpNew, existingWp)) // update WP (itself)
                reportWorkPlanPersistence.updateReportWorkPackage(workPackageId = wpId, wpNew.onlyWpChangesThemselves())

            updateNestedActivities( // update WP activities
                activitiesWithNewData = wpNew.activities,
                existingActivitiesById = existingWp.activities.associateBy { it.id },
            )
            updateNestedOutputs(    // update WP outputs
                outputsWithNewData = wpNew.outputs,
                existingOutputsById = existingWp.outputs.associateBy { it.id },
            )
            updateNestedInvestments(    // update WP investment
                investmentsWithNewData = wpNew.investments,
                existingInvestmentsById = existingWp.investments.associateBy { it.id },
            )
        }

        return reportWorkPlanPersistence.getReportWorkPlanById(projectId = projectId, reportId = reportId).fillInFlags()
    }

    private fun validateReportNotClosed(status: ProjectReportStatus) {
        if (status.isClosed())
            throw ReportAlreadyClosed()
    }

    private fun validateInputs(workPlan: List<ProjectReportWorkPackageUpdate>) {
        workPlan.forEach {
            generalValidator.throwIfAnyIsInvalid(
                generalValidator.maxLength(it.specificExplanation, 2000, "specificExplanation"),
                generalValidator.maxLength(it.communicationExplanation, 2000, "communicationExplanation"),
                generalValidator.maxLength(it.description, 2000, "description"),
            )

            it.activities.forEach {
                generalValidator.throwIfAnyIsInvalid(
                    generalValidator.maxLength(it.progress, 2000, "activity.progress"),
                )
                it.deliverables.forEach {
                    generalValidator.throwIfAnyIsInvalid(
                        generalValidator.maxLength(it.progress, 2000, "activity.deliverable.progress"),
                    )
                }
            }
            it.outputs.forEach {
                generalValidator.throwIfAnyIsInvalid(
                    generalValidator.maxLength(it.progress, 2000, "output.progress"),
                )
            }
        }
    }

    private fun updateNestedActivities(
        activitiesWithNewData: List<ProjectReportWorkPackageActivityUpdate>,
        existingActivitiesById: Map<Long, ProjectReportWorkPackageActivity>,
    ) {
        activitiesWithNewData.associateBy { it.id }.forEach { (activityId, activityNew) ->
            val existingActivity = existingActivitiesById.getByIdOrThrow(activityId)

            if (thereAreChanges(activityNew, existingActivity))
                reportWorkPlanPersistence.updateReportWorkPackageActivity(activityId = activityId, activityNew.status, activityNew.progress)

            updateNestedDeliverables(
                deliverablesWithNewData = activityNew.deliverables,
                existingDeliverablesById = existingActivity.deliverables.associateBy { it.id },
            )
        }
    }

    private fun updateNestedDeliverables(
        deliverablesWithNewData: List<ProjectReportWorkPackageActivityDeliverableUpdate>,
        existingDeliverablesById: Map<Long, ProjectReportWorkPackageActivityDeliverable>,
    ) {
        deliverablesWithNewData.associateBy { it.id }.forEach { (deliverableId, deliverableNew) ->
            val existingDeliverable = existingDeliverablesById.getByIdOrThrow(deliverableId)

            if (thereAreChanges(deliverableNew, existingDeliverable))
                reportWorkPlanPersistence.updateReportWorkPackageDeliverable(
                    deliverableId = deliverableId,
                    currentReport = deliverableNew.currentReport,
                    progress = deliverableNew.progress,
                )
        }
    }

    private fun updateNestedOutputs(
        outputsWithNewData: List<ProjectReportWorkPackageOutputUpdate>,
        existingOutputsById: Map<Long, ProjectReportWorkPackageOutput>,
    ) {
        outputsWithNewData.associateBy { it.id }.forEach { (outputId, outputNew) ->
            val existingOutput = existingOutputsById.getByIdOrThrow(outputId)

            if (thereAreChanges(outputNew, existingOutput))
                reportWorkPlanPersistence.updateReportWorkPackageOutput(
                    outputId = outputId,
                    currentReport = outputNew.currentReport,
                    progress = outputNew.progress,
                )
        }
    }

    private fun updateNestedInvestments(
        investmentsWithNewData: List<ProjectReportWorkPackageInvestmentUpdate>,
        existingInvestmentsById: Map<Long, ProjectReportWorkPackageInvestment>,
    ) {
        investmentsWithNewData.associateBy { it.id }.forEach { (investmentId, investmentNew) ->
            val existingOutput = existingInvestmentsById.getByIdOrThrow(investmentId)

            if (thereAreChanges(investmentNew, existingOutput))
                reportWorkPlanPersistence.updateReportWorkPackageInvestment(
                    investmentId = investmentId,
                    progress = investmentNew.progress,
                )
        }
    }

    private fun thereAreChanges(
        first: ProjectReportWorkPackageUpdate,
        second: ProjectReportWorkPackage,
    ) = first.specificStatus != second.specificStatus
        || first.specificExplanation != second.specificExplanation
        || first.communicationStatus != second.communicationStatus
        || first.communicationExplanation != second.communicationExplanation
        || first.completed != second.completed
        || first.description != second.description

    private fun thereAreChanges(
        first: ProjectReportWorkPackageActivityUpdate,
        second: ProjectReportWorkPackageActivity,
    ) = first.status != second.status || first.progress != second.progress

    private fun thereAreChanges(
        first: ProjectReportWorkPackageActivityDeliverableUpdate,
        second: ProjectReportWorkPackageActivityDeliverable,
    ) = first.currentReport != second.currentReport
        || first.progress != second.progress

    private fun thereAreChanges(
        first: ProjectReportWorkPackageOutputUpdate,
        second: ProjectReportWorkPackageOutput,
    ) = first.currentReport != second.currentReport
        || first.progress != second.progress

    private fun thereAreChanges(
        first: ProjectReportWorkPackageInvestmentUpdate,
        second: ProjectReportWorkPackageInvestment,
    ) = first.progress != second.progress

    private fun Map<Long, ProjectReportWorkPackage>.getByIdOrThrow(id: Long) = get(id)
        ?: throw WorkPackageNotFoundException(workPackageId = id)

    private fun Map<Long, ProjectReportWorkPackageActivity>.getByIdOrThrow(id: Long) = get(id)
        ?: throw WorkPackageActivityNotFoundException(activityId = id)

    private fun Map<Long, ProjectReportWorkPackageActivityDeliverable>.getByIdOrThrow(id: Long) = get(id)
        ?: throw WorkPackageActivityDeliverableNotFoundException(deliverableId = id)

    private fun Map<Long, ProjectReportWorkPackageOutput>.getByIdOrThrow(id: Long) = get(id)
        ?: throw WorkPackageOutputNotFoundException(outputId = id)

    private fun Map<Long, ProjectReportWorkPackageInvestment>.getByIdOrThrow(id: Long) = get(id)
        ?: throw WorkPackageInvestmentNotFoundException(investmentId = id)

    private fun ProjectReportWorkPackageUpdate.onlyWpChangesThemselves() = ProjectReportWorkPackageOnlyUpdate(
        specificStatus = specificStatus,
        specificExplanation = specificExplanation,
        communicationStatus = communicationStatus,
        communicationExplanation = communicationExplanation,
        completed = completed,
        description = description,
    )
}
