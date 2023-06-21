package io.cloudflight.jems.server.project.service.application.hand_back_to_applicant

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.authorization.ProjectAuthorization
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.notification.handler.ProjectStatusChangeEvent
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.application.workflow.states.ConditionsSubmittedApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.projectuser.assign_user_to_project.AssignUserToProjectEventListenersTest.Companion.project
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher

class HandBackToApplicantInteractorTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private val summary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callId = 1L,
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

        val slotAudit = mutableListOf<ProjectStatusChangeEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) }.returnsMany(Unit)

        assertThat(handBackToApplicant.handBackToApplicant(PROJECT_ID))
            .isEqualTo(ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS)

        verify (exactly = 1){ auditPublisher.publishEvent(slotAudit[0]) }

        assertThat(slotAudit[0]).isEqualTo(
            ProjectStatusChangeEvent(
                context = handBackToApplicant,
                projectSummary = summary,
                newStatus = ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS
            )
        )
    }

    @Test
    fun `handback when submitted precontracted checks modification permission`() {
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns project
        every { applicationStateFactory.getInstance(any()).handBackToApplicant() } returns ApplicationStatus.MODIFICATION_PRECONTRACTING_SUBMITTED

        assertThat(handBackToApplicant.handBackToApplicant(PROJECT_ID)).isEqualTo(ApplicationStatus.MODIFICATION_PRECONTRACTING_SUBMITTED)

        // verify - no? annotation
    }
}
