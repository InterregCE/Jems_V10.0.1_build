package io.cloudflight.jems.server.project.service.application.refuse_application

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.application.workflow.states.EligibleApplicationState
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
import java.time.LocalDate

class RefuseApplicationInteractorTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private val summary = ProjectSummary(
            id = PROJECT_ID,
            acronym = "project acronym",
            status = ApplicationStatus.ELIGIBLE
        )
        private val actionInfo = ApplicationActionInfo(
            note = "refusing application",
            date = LocalDate.of(2021, 4, 13)
        )
    }

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var applicationStateFactory: ApplicationStateFactory

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var refuseApplication: RefuseApplication

    @MockK
    lateinit var eligibleState: EligibleApplicationState


    @Test
    fun returnToApplicant() {
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns summary
        every { applicationStateFactory.getInstance(any()) } returns eligibleState
        every { eligibleState.refuse(actionInfo) } returns ApplicationStatus.NOT_APPROVED

        assertThat(refuseApplication.refuse(PROJECT_ID, actionInfo)).isEqualTo(ApplicationStatus.NOT_APPROVED)

        val slotAudit = slot<AuditCandidate>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_STATUS_CHANGED,
                project = AuditProject(id = PROJECT_ID.toString(), name = "project acronym"),
                description = "Project application status changed from ELIGIBLE to NOT_APPROVED"
            )
        )
    }

}
