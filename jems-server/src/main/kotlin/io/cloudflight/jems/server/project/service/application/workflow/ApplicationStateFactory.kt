package io.cloudflight.jems.server.project.service.application.workflow

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.*
import io.cloudflight.jems.server.project.service.application.workflow.states.ApprovedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ApprovedApplicationWithConditionsState
import io.cloudflight.jems.server.project.service.application.workflow.states.ConditionsSubmittedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.DraftApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.EligibleApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.InEligibleApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.NotApprovedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ReturnedToApplicantApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ReturnedToApplicantForConditionsApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.SubmittedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.first_step.FirstStepApprovedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.first_step.FirstStepApprovedApplicationWithConditionsState
import io.cloudflight.jems.server.project.service.application.workflow.states.first_step.FirstStepDraftApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.first_step.FirstStepEligibleApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.first_step.FirstStepIneligibleApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.first_step.FirstStepNotApprovedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.first_step.FirstStepSubmittedApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class ApplicationStateFactory(
    private val auditPublisher: ApplicationEventPublisher,
    private val securityService: SecurityService,
    private val projectPersistence: ProjectPersistence,
    private val projectWorkflowPersistence: ProjectWorkflowPersistence
) {

    fun getInstance(projectSummary: ProjectSummary) =
        when (projectSummary.status) {
            STEP1_DRAFT -> FirstStepDraftApplicationState(projectSummary,projectWorkflowPersistence, auditPublisher,securityService, projectPersistence)
            STEP1_SUBMITTED -> FirstStepSubmittedApplicationState(projectSummary,projectWorkflowPersistence, auditPublisher,securityService, projectPersistence)
            STEP1_ELIGIBLE -> FirstStepEligibleApplicationState(projectSummary,projectWorkflowPersistence, auditPublisher,securityService, projectPersistence)
            STEP1_INELIGIBLE -> FirstStepIneligibleApplicationState(projectSummary,projectWorkflowPersistence, auditPublisher,securityService, projectPersistence)
            STEP1_APPROVED -> FirstStepApprovedApplicationState(projectSummary,projectWorkflowPersistence, auditPublisher,securityService, projectPersistence)
            STEP1_APPROVED_WITH_CONDITIONS -> FirstStepApprovedApplicationWithConditionsState(projectSummary,projectWorkflowPersistence, auditPublisher,securityService, projectPersistence)
            STEP1_NOT_APPROVED -> FirstStepNotApprovedApplicationState(projectSummary,projectWorkflowPersistence, auditPublisher,securityService, projectPersistence)
            APPROVED -> ApprovedApplicationState(projectSummary,projectWorkflowPersistence, auditPublisher,securityService, projectPersistence)
            APPROVED_WITH_CONDITIONS -> ApprovedApplicationWithConditionsState(projectSummary, projectWorkflowPersistence, auditPublisher, securityService, projectPersistence)
            DRAFT -> DraftApplicationState(projectSummary,projectWorkflowPersistence,  auditPublisher,securityService, projectPersistence)
            ELIGIBLE -> EligibleApplicationState(projectSummary, projectWorkflowPersistence,  auditPublisher, securityService, projectPersistence)
            INELIGIBLE -> InEligibleApplicationState(projectSummary, projectWorkflowPersistence, auditPublisher, securityService, projectPersistence)
            NOT_APPROVED -> NotApprovedApplicationState(projectSummary, projectWorkflowPersistence, auditPublisher, securityService, projectPersistence)
            RETURNED_TO_APPLICANT -> ReturnedToApplicantApplicationState(projectSummary, projectWorkflowPersistence, auditPublisher, securityService, projectPersistence)
            RETURNED_TO_APPLICANT_FOR_CONDITIONS -> ReturnedToApplicantForConditionsApplicationState(projectSummary, projectWorkflowPersistence, auditPublisher, securityService, projectPersistence)
            SUBMITTED -> SubmittedApplicationState(projectSummary, projectWorkflowPersistence, auditPublisher, securityService, projectPersistence)
            CONDITIONS_SUBMITTED ->  ConditionsSubmittedApplicationState(projectSummary, projectWorkflowPersistence, auditPublisher, securityService, projectPersistence)
    }
}
