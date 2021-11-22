package io.cloudflight.jems.server.project.service.application.hand_back_to_applicant

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.authorization.ProjectAuthorization
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.application.workflow.states.ConditionsSubmittedApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher

class HandBackToApplicantInteractorTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private val summary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.CONDITIONS_SUBMITTED
        )
    }

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var projectWorkFlowPersistance: ProjectWorkflowPersistence

    @MockK
    lateinit var applicationStateFactory: ApplicationStateFactory

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var securityService: SecurityService

    @RelaxedMockK
    lateinit var projectAuthorization: ProjectAuthorization

    @InjectMockKs
    private lateinit var handBackToApplicant: HandBackToApplicant

    @MockK
    lateinit var conditionsSubmittedApplicationState: ConditionsSubmittedApplicationState

    @Test
    fun handBackToApplicant() {
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns summary
        every { applicationStateFactory.getInstance(any()) } returns conditionsSubmittedApplicationState
        every { conditionsSubmittedApplicationState.handBackToApplicant() } returns ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS

        val slotAudit = mutableListOf<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) }.returnsMany(Unit)

        Assertions.assertThat(handBackToApplicant.handBackToApplicant(PROJECT_ID))
            .isEqualTo(ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS)

        verify (exactly = 1){ auditPublisher.publishEvent(slotAudit[0]) }

        Assertions.assertThat(slotAudit[0].auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_STATUS_CHANGED,
                project = AuditProject(id = PROJECT_ID.toString(), customIdentifier = "01", name = "project acronym"),
                description = "Project application status changed from CONDITIONS_SUBMITTED to RETURNED_TO_APPLICANT_FOR_CONDITIONS"
            )
        )
    }
}
