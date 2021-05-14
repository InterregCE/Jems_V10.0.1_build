package io.cloudflight.jems.server.project.service.application.get_possible_status_to_revert_to

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.application.workflow.states.NotApprovedApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher

class GetPossibleStatusToRevertToInteractorTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private val summary = ProjectSummary(
            id = PROJECT_ID,
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.NOT_APPROVED
        )
    }

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var applicationStateFactory: ApplicationStateFactory

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var getPossibleStatusToRevertTo: GetPossibleStatusToRevertTo

    @MockK
    lateinit var notApprovedState: NotApprovedApplicationState


    @Test
    fun get() {
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns summary
        every { applicationStateFactory.getInstance(any()) } returns notApprovedState
        every { notApprovedState.getPossibleStatusToRevertTo() } returns ApplicationStatus.ELIGIBLE

        assertThat(getPossibleStatusToRevertTo.get(PROJECT_ID)).isEqualTo(ApplicationStatus.ELIGIBLE)
    }

}
