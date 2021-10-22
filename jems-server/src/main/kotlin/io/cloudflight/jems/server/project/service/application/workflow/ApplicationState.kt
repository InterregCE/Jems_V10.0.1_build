package io.cloudflight.jems.server.project.service.application.workflow

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.callAlreadyEnded
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDate

abstract class ApplicationState(
    protected open val projectSummary: ProjectSummary,
    protected open val projectWorkflowPersistence: ProjectWorkflowPersistence,
    protected open val auditPublisher: ApplicationEventPublisher,
    protected open val securityService: SecurityService,
    protected open val projectPersistence: ProjectPersistence
) {

    open fun submit(): ApplicationStatus =
        throw SubmitIsNotAllowedException(
            projectSummary.status
        )

    open fun setAsEligible(actionInfo: ApplicationActionInfo): ApplicationStatus =
        throw SetAsEligibleIsNotAllowedException(
            projectSummary.status
        )

    open fun setAsIneligible(actionInfo: ApplicationActionInfo): ApplicationStatus =
        throw SetAsIneligibleIsNotAllowedException(
            projectSummary.status
        )

    open fun approve(actionInfo: ApplicationActionInfo): ApplicationStatus =
        throw ApproveIsNotAllowedException(
            projectSummary.status
        )

    open fun approveWithConditions(actionInfo: ApplicationActionInfo): ApplicationStatus =
        throw ApproveWithConditionsIsNotAllowedException(
            projectSummary.status
        )

    open fun returnToApplicant(): ApplicationStatus =
        throw ReturnToApplicantIsNotAllowedException(
            projectSummary.status
        )

    open fun handBackToApplicant(): ApplicationStatus =
        throw HandBackToApplicantIsNotAllowedException(
            projectSummary.status
        )

    open fun startSecondStep(): ApplicationStatus =
        throw StartSecondStepIsNotAllowedException(
            projectSummary.status
        )

    open fun revertDecision(): ApplicationStatus =
        throw RevertLastActionOnApplicationIsNotAllowedException(
            projectSummary.status
        )

    open fun refuse(actionInfo: ApplicationActionInfo): ApplicationStatus =
        throw RefuseIsNotAllowedException(
            projectSummary.status
        )

    open fun getPossibleStatusToRevertTo(): ApplicationStatus? =
        null


    protected fun getPossibleStatusToRevertToDefaultImpl(validRevertStatuses: Set<ApplicationStatus>) =
        projectWorkflowPersistence.getApplicationPreviousStatus(projectSummary.id).let { previousStatus ->
            validRevertStatuses.firstOrNull { it === previousStatus.status }
        }

    protected fun returnToApplicantDefaultImpl(nextStatus: ApplicationStatus = ApplicationStatus.RETURNED_TO_APPLICANT) =
        projectWorkflowPersistence.updateProjectCurrentStatus(
            projectId = projectSummary.id,
            userId = securityService.getUserIdOrThrow(),
            status = nextStatus
        )

    protected fun startSecondStepDefaultImpl(): ApplicationStatus =
        isCallStep2Open().run {
            projectWorkflowPersistence.startSecondStep(
                projectId = projectSummary.id,
                userId = securityService.getUserIdOrThrow(),
            )
        }

    protected fun revertCurrentStatusToPreviousStatus(validRevertStatuses: Set<ApplicationStatus>): ApplicationStatus =
        projectWorkflowPersistence.getApplicationPreviousStatus(projectSummary.id).also { previousStatus ->

            if (!validRevertStatuses.contains(previousStatus.status))
                throw DecisionReversionIsNotPossibleException(projectSummary.status, previousStatus.status)

            projectWorkflowPersistence.revertCurrentStatusToPreviousStatus(projectSummary.id)

        }.status

    protected fun ifFundingDecisionDateIsValid(fundingDecisionDate: LocalDate?) {
        projectWorkflowPersistence.getProjectEligibilityDecisionDate(projectSummary.id).let { decisionDate ->
            if (decisionDate == null || fundingDecisionDate == null || fundingDecisionDate.isBefore(decisionDate))
                throw FundingDecisionIsBeforeEligibilityDecisionException()
        }
    }

    protected fun updateEligibilityDecision(targetStatus: ApplicationStatus, actionInfo: ApplicationActionInfo) =
        projectWorkflowPersistence.updateProjectEligibilityDecision(
            projectId = projectSummary.id,
            userId = securityService.getUserIdOrThrow(),
            status = targetStatus,
            actionInfo = actionInfo
        )

    protected fun updateFundingDecision(targetStatus: ApplicationStatus, actionInfo: ApplicationActionInfo) =
        ifFundingDecisionDateIsValid(actionInfo.date).run {
            projectWorkflowPersistence.updateProjectFundingDecision(
                projectSummary.id,
                securityService.getUserIdOrThrow(),
                targetStatus,
                actionInfo
            )
        }

    protected fun isCallStep1Open() =
        projectPersistence.getProjectCallSettings(projectSummary.id).also { projectCallSettings ->
            if (projectCallSettings.isCallStep1Closed()) {
                auditPublisher.publishEvent(callAlreadyEnded(this, projectCallSettings))

                throw CallIsNotOpenException()
            }
        }

    protected fun isCallStep2Open() =
        projectPersistence.getProjectCallSettings(projectSummary.id).also { projectCallSettings ->
            if (projectCallSettings.isCallStep2Closed()) {
                auditPublisher.publishEvent(callAlreadyEnded(this, projectCallSettings))

                throw CallIsNotOpenException()
            }
        }
}
