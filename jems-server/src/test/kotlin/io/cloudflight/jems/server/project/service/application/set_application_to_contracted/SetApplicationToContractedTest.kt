package io.cloudflight.jems.server.project.service.application.set_application_to_contracted

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.listOfApplicationStates
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.application.workflow.SetToContractedIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.states.ApprovedApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

internal class SetApplicationToContractedTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private val summary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.APPROVED
        )
    }

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var applicationStateFactory: ApplicationStateFactory

    @RelaxedMockK
    lateinit var projectWorkflowPersistence: ProjectWorkflowPersistence

    @RelaxedMockK
    lateinit var securityService: SecurityService

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var approvedState: ApprovedApplicationState

    @InjectMockKs
    lateinit var setApplicationToContracted: SetApplicationToContracted

    @Test
    fun `should set application to contracted when application is in approved state`() {
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns summary
        every { applicationStateFactory.getInstance(any()) } returns approvedState
        every { approvedState.setToContracted() } returns ApplicationStatus.CONTRACTED

        assertThat(setApplicationToContracted.setApplicationToContracted(PROJECT_ID)).isEqualTo(ApplicationStatus.CONTRACTED)

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_STATUS_CHANGED,
                project = AuditProject(id = PROJECT_ID.toString(), customIdentifier = "01", name = "project acronym"),
                description = "Project application status changed from APPROVED to CONTRACTED"
            )
        )
    }

    @TestFactory
    fun `should throw SetToContractedIsNotAllowedException when application is no in the approved state`() =
        listOfApplicationStates().filterNot { it.first == ApplicationStatus. APPROVED}
        .map { input ->
            DynamicTest.dynamicTest(
                "should throw SetToContractedIsNotAllowedException when application is in `${input.first}` state"
            ) {
                every { projectPersistence.getProjectSummary(PROJECT_ID) } returns summary
                every { applicationStateFactory.getInstance(any()) } returns input.second
                every { approvedState.setToContracted() } returns ApplicationStatus.CONTRACTED

                assertThrows<SetToContractedIsNotAllowedException> {  setApplicationToContracted.setApplicationToContracted(PROJECT_ID) }
            }
        }
}
