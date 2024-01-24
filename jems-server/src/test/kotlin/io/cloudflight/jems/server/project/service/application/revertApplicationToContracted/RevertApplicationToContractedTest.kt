package io.cloudflight.jems.server.project.service.application.revertApplicationToContracted

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.authorization.ProjectAuthorization
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.notification.handler.ProjectStatusChangeEvent
import io.cloudflight.jems.server.project.service.ProjectAssessmentPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher

class RevertApplicationToContractedTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 46L

        private val summary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callId = 8L,
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.CLOSED,
        )

    }

    @MockK private lateinit var auditPublisher: ApplicationEventPublisher
    @MockK private lateinit var securityService: SecurityService
    @MockK private lateinit var projectPersistence: ProjectPersistence
    @MockK private lateinit var projectWorkflowPersistence: ProjectWorkflowPersistence
    @MockK private lateinit var projectAuthorization: ProjectAuthorization
    @MockK private lateinit var projectAssessmentPersistence: ProjectAssessmentPersistence
    @MockK private lateinit var projectVersionPersistence: ProjectVersionPersistence
    @MockK private lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @InjectMockKs
    private lateinit var applicationStateFactory: ApplicationStateFactory

    @InjectMockKs
    private lateinit var interactor: RevertApplicationToContracted

    @BeforeEach
    fun setup() {
        clearMocks(auditPublisher, projectPersistence, securityService)
    }

    @Test
    fun revertApplicationToContracted() {
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns summary
        every { securityService.getUserIdOrThrow() } returns 4L
        every {
            projectWorkflowPersistence.updateProjectCurrentStatus(PROJECT_ID, 4L, ApplicationStatus.CONTRACTED, null)
        } returnsArgument 2

        val slotAuditStatus = slot<ProjectStatusChangeEvent>()
        every { auditPublisher.publishEvent(capture(slotAuditStatus)) } answers { }

        assertThat(interactor.revertApplicationToContracted(PROJECT_ID)).isEqualTo(ApplicationStatus.CONTRACTED)

        verify(exactly = 1) { auditPublisher.publishEvent(any<ProjectStatusChangeEvent>()) }

        assertThat(slotAuditStatus.captured).isEqualTo(
            ProjectStatusChangeEvent(interactor, projectSummary = summary, newStatus = ApplicationStatus.CONTRACTED)
        )
    }

}
