package io.cloudflight.jems.server.project.service.application.workflow

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.APPROVED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.APPROVED_WITH_CONDITIONS
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.DRAFT
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.ELIGIBLE
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.INELIGIBLE
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.NOT_APPROVED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.RETURNED_TO_APPLICANT
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.SUBMITTED
import io.cloudflight.jems.server.project.service.application.workflow.states.ApprovedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ApprovedApplicationWithConditionsState
import io.cloudflight.jems.server.project.service.application.workflow.states.DraftApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.EligibleApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.InEligibleApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.NotApprovedApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ReturnedToApplicantApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.SubmittedApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import org.springframework.stereotype.Service

@Service
class ApplicationStateFactory(
    private val auditService: AuditService,
    private val securityService: SecurityService,
    private val projectPersistence: ProjectPersistence
) {

    fun getInstance(projectSummary: ProjectSummary) =
        when (projectSummary.status) {
            APPROVED -> ApprovedApplicationState(projectSummary,projectPersistence, auditService,securityService)
            APPROVED_WITH_CONDITIONS -> ApprovedApplicationWithConditionsState(projectSummary, projectPersistence, auditService, securityService)
            DRAFT -> DraftApplicationState(projectSummary,projectPersistence,  auditService,securityService)
            ELIGIBLE -> EligibleApplicationState(projectSummary, projectPersistence,  auditService, securityService)
            INELIGIBLE -> InEligibleApplicationState(projectSummary, projectPersistence, auditService, securityService)
            NOT_APPROVED -> NotApprovedApplicationState(projectSummary, projectPersistence, auditService, securityService)
            RETURNED_TO_APPLICANT -> ReturnedToApplicantApplicationState(projectSummary, projectPersistence, auditService, securityService)
            SUBMITTED -> SubmittedApplicationState(projectSummary, projectPersistence, auditService, securityService)
        }
}
