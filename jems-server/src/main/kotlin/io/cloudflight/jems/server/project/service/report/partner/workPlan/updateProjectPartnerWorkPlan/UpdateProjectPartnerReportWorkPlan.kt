package io.cloudflight.jems.server.project.service.report.partner.workPlan.updateProjectPartnerWorkPlan

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.model.workPlan.update.UpdateProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.workPlan.update.UpdateProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.workPlan.update.UpdateProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.workPlan.update.UpdateProjectPartnerReportWorkPackageOutput
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectPartnerReportWorkPlan(
    private val reportPersistence: ProjectReportPersistence,
    private val generalValidator: GeneralValidatorService,
) : UpdateProjectPartnerReportWorkPlanInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerReportWorkPlanException::class)
    override fun update(
        partnerId: Long,
        reportId: Long,
        workPlan: List<UpdateProjectPartnerReportWorkPackage>
    ): List<ProjectPartnerReportWorkPackage> {
        validateReportNotClosed(status = reportPersistence.getPartnerReportStatusById(partnerId, reportId = reportId))
        validateInputs(workPlan = workPlan)

        val existingWpsById = reportPersistence.getPartnerReportWorkPlanById(partnerId = partnerId, reportId = reportId)
            .associateBy { it.id }

        workPlan.associateBy { it.id }.forEach { (wpId, wpNew) ->
            val existingWp = existingWpsById.getByIdOrThrow(wpId)

            if (thereAreChanges(wpNew, existingWp)) // update WP (itself)
                reportPersistence.updatePartnerReportWorkPackage(workPackageId = wpId, translations = wpNew.description)

            updateNestedActivities( // update WP activities
                activitiesWithNewData = wpNew.activities,
                existingActivitiesById = existingWp.activities.associateBy { it.id },
            )
            updateNestedOutputs(    // update WP outputs
                outputsWithNewData = wpNew.outputs,
                existingOutputsById = existingWp.outputs.associateBy { it.id },
            )
        }

        return reportPersistence.getPartnerReportWorkPlanById(partnerId = partnerId, reportId = reportId)
    }

    private fun validateReportNotClosed(status: ReportStatus) {
        if (status.isClosed())
            throw ReportAlreadyClosed()
    }

    private fun validateInputs(workPlan: List<UpdateProjectPartnerReportWorkPackage>) {
        workPlan.forEach {
            generalValidator.throwIfAnyIsInvalid(
                generalValidator.maxLength(it.description, 2000, "description"),
            )

            it.activities.forEach {
                generalValidator.throwIfAnyIsInvalid(
                    generalValidator.maxLength(it.progress, 2000, "progress"),
                )
            }
        }
    }

    private fun updateNestedActivities(
        activitiesWithNewData: List<UpdateProjectPartnerReportWorkPackageActivity>,
        existingActivitiesById: Map<Long, ProjectPartnerReportWorkPackageActivity>,
    ) {
        activitiesWithNewData.associateBy { it.id }.forEach { (activityId, activityNew) ->
            val existingActivity = existingActivitiesById.getByIdOrThrow(activityId)

            if (thereAreChanges(activityNew, existingActivity))
                reportPersistence.updatePartnerReportWorkPackageActivity(activityId = activityId, translations = activityNew.progress)

            updateNestedDeliverables(
                deliverablesWithNewData = activityNew.deliverables,
                existingDeliverablesById = existingActivity.deliverables.associateBy { it.id },
            )
        }
    }

    private fun updateNestedDeliverables(
        deliverablesWithNewData: List<UpdateProjectPartnerReportWorkPackageActivityDeliverable>,
        existingDeliverablesById: Map<Long, ProjectPartnerReportWorkPackageActivityDeliverable>,
    ) {
        deliverablesWithNewData.associateBy { it.id }.forEach { (deliverableId, deliverableNew) ->
            val existingDeliverable = existingDeliverablesById.getByIdOrThrow(deliverableId)

            if (thereAreChanges(deliverableNew, existingDeliverable))
                reportPersistence.updatePartnerReportWorkPackageDeliverable(
                    deliverableId = deliverableId,
                    contribution = deliverableNew.contribution,
                    evidence = deliverableNew.evidence,
                )
        }
    }

    private fun updateNestedOutputs(
        outputsWithNewData: List<UpdateProjectPartnerReportWorkPackageOutput>,
        existingOutputsById: Map<Long, ProjectPartnerReportWorkPackageOutput>,
    ) {
        outputsWithNewData.associateBy { it.id }.forEach { (outputId, outputNew) ->
            val existingOutput = existingOutputsById.getByIdOrThrow(outputId)

            if (thereAreChanges(outputNew, existingOutput))
                reportPersistence.updatePartnerReportWorkPackageOutput(
                    outputId = outputId,
                    contribution = outputNew.contribution,
                    evidence = outputNew.evidence,
                )
        }
    }

    private fun thereAreChanges(
        first: UpdateProjectPartnerReportWorkPackage,
        second: ProjectPartnerReportWorkPackage,
    ) = first.description != second.description

    private fun thereAreChanges(
        first: UpdateProjectPartnerReportWorkPackageActivity,
        second: ProjectPartnerReportWorkPackageActivity,
    ) = first.progress != second.progress

    private fun thereAreChanges(
        first: UpdateProjectPartnerReportWorkPackageActivityDeliverable,
        second: ProjectPartnerReportWorkPackageActivityDeliverable,
    ) = first.contribution != second.contribution
        || first.evidence != second.evidence

    private fun thereAreChanges(
        first: UpdateProjectPartnerReportWorkPackageOutput,
        second: ProjectPartnerReportWorkPackageOutput,
    ) = first.contribution != second.contribution
        || first.evidence != second.evidence

    private fun Map<Long, ProjectPartnerReportWorkPackage>.getByIdOrThrow(id: Long) = get(id)
        ?: throw WorkPackageNotFoundException(workPackageId = id)

    private fun Map<Long, ProjectPartnerReportWorkPackageActivity>.getByIdOrThrow(id: Long) = get(id)
        ?: throw WorkPackageActivityNotFoundException(activityId = id)

    private fun Map<Long, ProjectPartnerReportWorkPackageActivityDeliverable>.getByIdOrThrow(id: Long) = get(id)
        ?: throw WorkPackageActivityDeliverableNotFoundException(deliverableId = id)

    private fun Map<Long, ProjectPartnerReportWorkPackageOutput>.getByIdOrThrow(id: Long) = get(id)
        ?: throw WorkPackageOutputNotFoundException(outputId = id)

}
