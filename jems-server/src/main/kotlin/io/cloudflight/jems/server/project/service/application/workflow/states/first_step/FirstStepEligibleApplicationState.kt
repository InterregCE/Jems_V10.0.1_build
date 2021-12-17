package io.cloudflight.jems.server.project.service.application.workflow.states.first_step

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectAssessmentPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import org.springframework.context.ApplicationEventPublisher

class FirstStepEligibleApplicationState(
    override val projectSummary: ProjectSummary,
    override val projectWorkflowPersistence: ProjectWorkflowPersistence,
    override val auditPublisher: ApplicationEventPublisher,
    override val securityService: SecurityService,
    override val projectPersistence: ProjectPersistence,
    private val projectAssessmentPersistence: ProjectAssessmentPersistence
) : ApplicationState(projectSummary, projectWorkflowPersistence, auditPublisher, securityService, projectPersistence) {

    private val canBeRevertTo = setOf(ApplicationStatus.STEP1_SUBMITTED)

    override fun revertDecision(): ApplicationStatus =
        revertCurrentStatusToPreviousStatus(canBeRevertTo).also {
            projectWorkflowPersistence.clearProjectEligibilityDecision(projectSummary.id)
        }

    override fun getPossibleStatusToRevertTo() =
        getPossibleStatusToRevertToDefaultImpl(canBeRevertTo)

    override fun approve(actionInfo: ApplicationActionInfo): ApplicationStatus =
        throwIfQualityAssessmentIsMissing(projectAssessmentPersistence, projectSummary.id, projectSummary.status).let {
            updateFundingDecision(ApplicationStatus.STEP1_APPROVED, actionInfo)
        }

    override fun approveWithConditions(actionInfo: ApplicationActionInfo): ApplicationStatus =
        throwIfQualityAssessmentIsMissing(projectAssessmentPersistence, projectSummary.id, projectSummary.status).let {
            updateFundingDecision(ApplicationStatus.STEP1_APPROVED_WITH_CONDITIONS, actionInfo)
        }

    override fun refuse(actionInfo: ApplicationActionInfo): ApplicationStatus =
        throwIfQualityAssessmentIsMissing(projectAssessmentPersistence, projectSummary.id, projectSummary.status).let {
            updateFundingDecision(ApplicationStatus.STEP1_NOT_APPROVED, actionInfo)
        }

}
