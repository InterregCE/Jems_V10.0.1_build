package io.cloudflight.jems.server.project.service.application.start_second_step

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.application.workflow.states.ApprovedApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher

class StartSecondStepInteractorTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private val summary = ProjectSummary(
            id = PROJECT_ID,
            acronym = "project acronym",
            status = ApplicationStatus.STEP1_APPROVED
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
    lateinit var securityService: SecurityService

    @InjectMockKs
    private lateinit var startSecondStep: StartSecondStep

    @MockK
    lateinit var approvedState: ApprovedApplicationState

    @Test
    fun startSecondStep() {
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns summary
        every { applicationStateFactory.getInstance(any()) } returns approvedState
        every { approvedState.startSecondStep() } returns ApplicationStatus.DRAFT

        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) }.returnsMany(Unit)

        assertThat(startSecondStep.startSecondStep(PROJECT_ID)).isEqualTo(ApplicationStatus.DRAFT)

        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }

        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_STATUS_CHANGED,
                project = AuditProject(id = PROJECT_ID.toString(), name = "project acronym"),
                description = "Project application status changed from STEP1_APPROVED to DRAFT"
            )
        )
    }

}
