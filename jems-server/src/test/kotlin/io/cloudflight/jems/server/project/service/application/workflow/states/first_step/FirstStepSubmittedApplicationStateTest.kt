package io.cloudflight.jems.server.project.service.application.workflow.states.first_step

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApproveIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.ApproveWithConditionsIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.RefuseIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.ReturnToApplicantIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.RevertLastActionOnApplicationIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.SubmitIsNotAllowedException
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDate

class FirstStepSubmittedApplicationStateTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 4L
        private const val USER_ID = 9L

        private val actionInfo = ApplicationActionInfo(
            note = "some dummy note",
            date = LocalDate.now()
        )
    }

    @MockK
    lateinit var projectSummary: ProjectSummary

    @MockK
    lateinit var projectWorkflowPersistence: ProjectWorkflowPersistence

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    private lateinit var submittedApplicationState: FirstStepSubmittedApplicationState

    @BeforeAll
    fun setup() {
        every { projectSummary.id } returns PROJECT_ID
        every { projectSummary.status } returns ApplicationStatus.STEP1_SUBMITTED
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @ParameterizedTest
    @EnumSource(value = ApplicationStatus::class, names = ["STEP1_ELIGIBLE", "STEP1_INELIGIBLE"])
    fun `should have state STEP1_ELIGIBLE or STEP1_INELIGIBLE when submitting application`(status: ApplicationStatus) {
        every { projectWorkflowPersistence.updateProjectEligibilityDecision(any(), any(), any(), any()) } returns status
        if (status == ApplicationStatus.STEP1_ELIGIBLE)
            assertThat(submittedApplicationState.setAsEligible(actionInfo)).isEqualTo(status)
        else
            assertThat(submittedApplicationState.setAsIneligible(actionInfo)).isEqualTo(status)

        verify(exactly = 1) {
            projectWorkflowPersistence.updateProjectEligibilityDecision(PROJECT_ID, USER_ID, status, actionInfo) }
    }

    @Test
    fun `should throw SubmitIsNotAllowedException when submitting application`() {
        assertThrows<SubmitIsNotAllowedException> { submittedApplicationState.submit() }
    }

    @Test
    fun `should throw ApproveIsNotAllowedException when approving application`() {
        assertThrows<ApproveIsNotAllowedException> { submittedApplicationState.approve(actionInfo) }
    }

    @Test
    fun `should throw ApproveWithConditionsIsNotAllowedException when approving application with conditions`() {
        assertThrows<ApproveWithConditionsIsNotAllowedException> {
            submittedApplicationState.approveWithConditions(actionInfo)
        }
    }

    @Test
    fun `should throw ReturnToApplicantIsNotAllowedException when returning application to applicant`() {
        assertThrows<ReturnToApplicantIsNotAllowedException> { submittedApplicationState.returnToApplicant() }
    }

    @Test
    fun `should throw RevertLastActionOnApplicationIsNotAllowedException when reverting decision`() {
        assertThrows<RevertLastActionOnApplicationIsNotAllowedException> { submittedApplicationState.revertDecision() }
    }

    @Test
    fun `should throw RefuseIsNotAllowedException when refusing application`() {
        assertThrows<RefuseIsNotAllowedException> { submittedApplicationState.refuse(actionInfo) }
    }

    @Test
    fun `should get possible status when reverting decision`() {
        assertThat(submittedApplicationState.getPossibleStatusToRevertTo()).isNull()
    }

}
