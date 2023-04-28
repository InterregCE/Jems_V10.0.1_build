package io.cloudflight.jems.server.project.service.application.set_application_as_eligible

import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentEligibilityResult
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.projectWithId
import io.cloudflight.jems.server.notification.handler.ProjectStatusChangeEvent
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.application.workflow.states.SubmittedApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectAssessment
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentEligibility
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDate

class SetApplicationAsEligibleInteractorTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private val summary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callName = "",
            callId = 1L,
            acronym = "project acronym",
            status = ApplicationStatus.SUBMITTED
        )
        private val actionInfo = ApplicationActionInfo(
            note = "note eligible",
            date = LocalDate.of(2021,4, 13),
            entryIntoForceDate = LocalDate.of(2021,4, 13)
        )
    }

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var applicationStateFactory: ApplicationStateFactory

    @RelaxedMockK
    lateinit var generalValidatorService: GeneralValidatorService

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var setApplicationAsEligible: SetApplicationAsEligible

    @MockK
    lateinit var submittedState: SubmittedApplicationState


    @Test
    fun setAsEligible() {
        every { projectPersistence.getProject(PROJECT_ID) } returns projectWithId(PROJECT_ID).copy(
            assessmentStep2 = ProjectAssessment(assessmentEligibility = ProjectAssessmentEligibility(PROJECT_ID, 2, ProjectAssessmentEligibilityResult.PASSED))
        )
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns summary
        every { applicationStateFactory.getInstance(any()) } returns submittedState
        every { submittedState.setAsEligible(actionInfo) } returns ApplicationStatus.ELIGIBLE

        assertThat(setApplicationAsEligible.setAsEligible(PROJECT_ID, actionInfo)).isEqualTo(ApplicationStatus.ELIGIBLE)

        val slotAudit = slot<ProjectStatusChangeEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured).isEqualTo(
            ProjectStatusChangeEvent(
                context = setApplicationAsEligible,
                projectSummary = summary,
                newStatus = ApplicationStatus.ELIGIBLE
            )
        )
    }

    @Test
    fun setAsEligibleException() {
        every { projectPersistence.getProject(PROJECT_ID) } returns projectWithId(PROJECT_ID)
        assertThrows<EligibilityAssessmentMissing> { setApplicationAsEligible.setAsEligible(PROJECT_ID, actionInfo) }
    }
}
