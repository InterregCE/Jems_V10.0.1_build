package io.cloudflight.jems.server.project.service.application.revert_application_decision

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.submit_application.ProjectStatusChangeEvent
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.application.workflow.states.EligibleApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher

class RevertApplicationDecisionInteractorTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private val summary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callId = 1L,
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.ELIGIBLE
        )
    }

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var applicationStateFactory: ApplicationStateFactory

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var revertApplicationDecision: RevertApplicationDecision

    @MockK
    lateinit var eligibleState: EligibleApplicationState


    @Test
    fun revert() {
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns summary
        every { applicationStateFactory.getInstance(any()) } returns eligibleState
        every { eligibleState.revertDecision() } returns ApplicationStatus.SUBMITTED

        assertThat(revertApplicationDecision.revert(PROJECT_ID)).isEqualTo(ApplicationStatus.SUBMITTED)

        val slotAudit = mutableListOf<ProjectStatusChangeEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) }.returns(Unit)
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit[0]).isEqualTo(
            ProjectStatusChangeEvent(
                context = revertApplicationDecision,
                projectSummary = summary,
                newStatus = ApplicationStatus.SUBMITTED
            )
        )
    }

}
