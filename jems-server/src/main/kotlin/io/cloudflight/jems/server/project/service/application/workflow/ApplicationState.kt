package io.cloudflight.jems.server.project.service.application.workflow

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import java.time.LocalDate

abstract class ApplicationState(
    protected open val projectSummary: ProjectSummary,
    protected open val projectWorkflowPersistence: ProjectWorkflowPersistence,
    protected open val auditService: AuditService,
    protected open val securityService: SecurityService
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

    open fun startSecondStep(): ApplicationStatus =
        throw ReturnToApplicantIsNotAllowedException(
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
            validRevertStatuses.firstOrNull { it === previousStatus }
        }

    protected fun returnToApplicantDefaultImpl(): ApplicationStatus =
        projectWorkflowPersistence.updateProjectCurrentStatus(
            projectId = projectSummary.id,
            userId = securityService.getUserIdOrThrow(),
            status = ApplicationStatus.RETURNED_TO_APPLICANT
        )

    protected fun startSecondStepDefaultImpl(): ApplicationStatus =
        projectWorkflowPersistence.startSecondStep(
            projectId = projectSummary.id,
            userId = securityService.getUserIdOrThrow(),
        )

    protected fun revertCurrentStatusToPreviousStatus(validRevertStatuses: Set<ApplicationStatus>): ApplicationStatus =
        projectWorkflowPersistence.getApplicationPreviousStatus(projectSummary.id).also { previousStatus ->

            if (!validRevertStatuses.contains(previousStatus))
                throw DecisionReversionIsNotPossibleException(projectSummary.status, previousStatus)

            projectWorkflowPersistence.revertCurrentStatusToPreviousStatus(projectSummary.id)

        }

    protected fun ifFundingDecisionDateIsValid(fundingDecisionDate: LocalDate?) {
        projectWorkflowPersistence.getProjectEligibilityDecisionDate(projectSummary.id).let { decisionDate ->
            if (decisionDate == null || fundingDecisionDate == null || fundingDecisionDate.isBefore(decisionDate))
                throw FundingDecisionIsBeforeEligibilityDecisionException()
        }
    }

}
