package io.cloudflight.jems.server.project.service.application.workflow

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.authorization.ProjectAuthorization
import io.cloudflight.jems.server.project.service.ProjectAssessmentPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.APPROVED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.APPROVED_WITH_CONDITIONS
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.CONDITIONS_SUBMITTED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.CONTRACTED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.DRAFT
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.ELIGIBLE
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.INELIGIBLE
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.IN_MODIFICATION
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.MODIFICATION_PRECONTRACTING
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.MODIFICATION_PRECONTRACTING_SUBMITTED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.MODIFICATION_REJECTED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.MODIFICATION_SUBMITTED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.NOT_APPROVED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.RETURNED_TO_APPLICANT
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_APPROVED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_APPROVED_WITH_CONDITIONS
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_DRAFT
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_ELIGIBLE
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_INELIGIBLE
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_NOT_APPROVED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_SUBMITTED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.SUBMITTED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.CLOSED
import io.cloudflight.jems.server.project.service.application.workflow.states.ApprovedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ApprovedApplicationWithConditionsState
import io.cloudflight.jems.server.project.service.application.workflow.states.ClosedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ConditionsSubmittedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ContractedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.DraftApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.EligibleApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.InEligibleApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.InModificationApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ModificationPreContractingApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ModificationPreContractingSubmittedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ModificationRejectedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ModificationSubmittedApplicationState
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
    private val projectWorkflowPersistence: ProjectWorkflowPersistence,
    private val projectAuthorization: ProjectAuthorization,
    private val projectAssessmentPersistence: ProjectAssessmentPersistence,
    private val projectVersionPersistence: ProjectVersionPersistence,
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence
) {

    fun getInstance(projectSummary: ProjectSummary) =
        when (projectSummary.status) {
            STEP1_DRAFT -> FirstStepDraftApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
            )
            STEP1_SUBMITTED -> FirstStepSubmittedApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
            )
            STEP1_ELIGIBLE -> FirstStepEligibleApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
                projectAssessmentPersistence,
            )
            STEP1_INELIGIBLE -> FirstStepIneligibleApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
            )
            STEP1_APPROVED -> FirstStepApprovedApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
            )
            STEP1_APPROVED_WITH_CONDITIONS -> FirstStepApprovedApplicationWithConditionsState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
            )
            STEP1_NOT_APPROVED -> FirstStepNotApprovedApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
            )
            APPROVED -> ApprovedApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
                projectVersionPersistence,
                controllerInstitutionPersistence
            )
            APPROVED_WITH_CONDITIONS -> ApprovedApplicationWithConditionsState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
            )
            DRAFT -> DraftApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
            )
            ELIGIBLE -> EligibleApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
                projectAssessmentPersistence,
            )
            INELIGIBLE -> InEligibleApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
            )
            NOT_APPROVED -> NotApprovedApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
            )
            RETURNED_TO_APPLICANT -> ReturnedToApplicantApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
            )
            RETURNED_TO_APPLICANT_FOR_CONDITIONS -> ReturnedToApplicantForConditionsApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
            )
            SUBMITTED -> SubmittedApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
            )
            CONDITIONS_SUBMITTED -> ConditionsSubmittedApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
                projectAuthorization,
            )
            MODIFICATION_PRECONTRACTING -> ModificationPreContractingApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
            )
            MODIFICATION_PRECONTRACTING_SUBMITTED -> ModificationPreContractingSubmittedApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
                projectAuthorization,
            )
            IN_MODIFICATION -> InModificationApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
            )
            MODIFICATION_SUBMITTED -> ModificationSubmittedApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
                projectAuthorization,
            )
            MODIFICATION_REJECTED -> ModificationRejectedApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
            )
            CONTRACTED -> ContractedApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
            )
            CLOSED -> ClosedApplicationState(
                projectSummary,
                projectWorkflowPersistence,
                auditPublisher,
                securityService,
                projectPersistence,
            )
    }
}
