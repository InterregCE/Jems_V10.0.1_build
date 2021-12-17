package io.cloudflight.jems.server.project.service.application.return_application_to_applicant

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.authorization.ProjectAuthorization
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.hand_back_to_applicant.HandBackToApplicant
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.application.workflow.states.ApprovedApplicationWithConditionsState
import io.cloudflight.jems.server.project.service.application.workflow.states.SubmittedApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.save_project_version.CreateNewProjectVersionInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher

class ReturnApplicationToApplicantInteractorTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private val summary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.SUBMITTED
        )
    }

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var applicationStateFactory: ApplicationStateFactory

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var projectVersionPersistence: ProjectVersionPersistence

    @RelaxedMockK
    lateinit var projectWorkflowPersistance: ProjectWorkflowPersistence

    @RelaxedMockK
    lateinit var securityService: SecurityService

    @RelaxedMockK
    lateinit var projectAuthorization: ProjectAuthorization

    @RelaxedMockK
    lateinit var createNewProjectVersion: CreateNewProjectVersionInteractor

    @InjectMockKs
    private lateinit var returnApplicationToApplicant: ReturnApplicationToApplicant

    @InjectMockKs
    private lateinit var handBackToApplicant: HandBackToApplicant

    @MockK
    lateinit var submittedState: SubmittedApplicationState

    @MockK
    lateinit var approvedWithConditionsState: ApprovedApplicationWithConditionsState


    @Test
    fun returnToApplicant() {
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns summary
        every { applicationStateFactory.getInstance(any()) } returns submittedState
        every { submittedState.returnToApplicant() } returns ApplicationStatus.RETURNED_TO_APPLICANT

        val slotAudit = mutableListOf<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) }.returnsMany(Unit)

        assertThat(returnApplicationToApplicant.returnToApplicant(PROJECT_ID)).isEqualTo(ApplicationStatus.RETURNED_TO_APPLICANT)

        verify (exactly = 2){ auditPublisher.publishEvent(or(slotAudit[0], slotAudit[1])) }

        assertThat(slotAudit[0].auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_STATUS_CHANGED,
                project = AuditProject(id = PROJECT_ID.toString(), customIdentifier = "01", name = "project acronym"),
                description = "Project application status changed from SUBMITTED to RETURNED_TO_APPLICANT"
            )
        )

        assertThat(slotAudit[1].auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_VERSION_RECORDED,
                project = AuditProject(id = PROJECT_ID.toString(), customIdentifier = "01", name = "project acronym"),
                description = slotAudit[1].auditCandidate.description
            )
        )
    }

    @Test
    fun returnToApplicantFromApprovedWithConditions() {
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns summary
        every { applicationStateFactory.getInstance(any()) } returns approvedWithConditionsState
        every { approvedWithConditionsState.returnToApplicant() } returns ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS

        val slotAudit = mutableListOf<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) }.returnsMany(Unit)

        assertThat(returnApplicationToApplicant.returnToApplicant(PROJECT_ID)).isEqualTo(ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS)

        verify (exactly = 2){ auditPublisher.publishEvent(or(slotAudit[0], slotAudit[1])) }

        assertThat(slotAudit[0].auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_STATUS_CHANGED,
                project = AuditProject(id = PROJECT_ID.toString(), customIdentifier = "01", name = "project acronym"),
                description = "Project application status changed from SUBMITTED to RETURNED_TO_APPLICANT_FOR_CONDITIONS"
            )
        )

        assertThat(slotAudit[1].auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_VERSION_RECORDED,
                project = AuditProject(id = PROJECT_ID.toString(), customIdentifier = "01", name = "project acronym"),
                description = slotAudit[1].auditCandidate.description
            )
        )
    }

}
