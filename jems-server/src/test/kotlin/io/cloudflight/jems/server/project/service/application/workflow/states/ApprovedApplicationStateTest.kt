package io.cloudflight.jems.server.project.service.application.workflow.states

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApproveIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.ApproveWithConditionsIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.DecisionReversionIsNotPossibleException
import io.cloudflight.jems.server.project.service.application.workflow.RefuseIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.SetAsEligibleIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.SetAsIneligibleIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.SubmitIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.states.ProjectStatusTestUtil.Companion.getStatusModelForStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class ApprovedApplicationStateTest {

    companion object {
        private const val PROJECT_ID = 8L
        private const val USER_ID = 10L

        private val actionInfo = ApplicationActionInfo(
            note = "some dummy note",
            date = LocalDate.now(),
            entryIntoForceDate = LocalDate.now()
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
    private lateinit var approvedApplicationState: ApprovedApplicationState

    @BeforeEach
    fun setup() {
        every { projectSummary.id } returns PROJECT_ID
        every { projectSummary.status } returns ApplicationStatus.APPROVED
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @Test
    fun returnToApplicant() {
        every { projectWorkflowPersistence.updateProjectCurrentStatus(any(), any(), any()) } returns ApplicationStatus.RETURNED_TO_APPLICANT

        assertThat(approvedApplicationState.returnToApplicant()).isEqualTo(ApplicationStatus.RETURNED_TO_APPLICANT)
        verify(exactly = 1) { projectWorkflowPersistence.updateProjectCurrentStatus(PROJECT_ID, USER_ID, ApplicationStatus.RETURNED_TO_APPLICANT)  }
    }

    @Test
    fun `revertDecision to APPROVED_WITH_CONDITIONS`() {
        val status = ApplicationStatus.APPROVED_WITH_CONDITIONS
        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns getStatusModelForStatus(status)
        every { projectWorkflowPersistence.revertCurrentStatusToPreviousStatus(PROJECT_ID) } returns status
        every { projectWorkflowPersistence.resetProjectFundingDecisionToCurrentStatus(PROJECT_ID) } returns status

        assertThat(approvedApplicationState.revertDecision()).isEqualTo(status)
        verify(exactly = 1) { projectWorkflowPersistence.revertCurrentStatusToPreviousStatus(PROJECT_ID)  }
        verify(exactly = 1) { projectWorkflowPersistence.resetProjectFundingDecisionToCurrentStatus(PROJECT_ID)  }
    }

    @Test
    fun `revertDecision to ELIGIBLE`() {
        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns getStatusModelForStatus(ApplicationStatus.ELIGIBLE)
        every { projectWorkflowPersistence.revertCurrentStatusToPreviousStatus(PROJECT_ID) } returns ApplicationStatus.ELIGIBLE
        every { projectWorkflowPersistence.clearProjectFundingDecision(PROJECT_ID) } answers { }

        assertThat(approvedApplicationState.revertDecision()).isEqualTo(ApplicationStatus.ELIGIBLE)
        verify(exactly = 1) { projectWorkflowPersistence.revertCurrentStatusToPreviousStatus(PROJECT_ID)  }
        verify(exactly = 1) { projectWorkflowPersistence.clearProjectFundingDecision(PROJECT_ID)  }
    }

    @ParameterizedTest(name = "revertDecision to {0} - invalid")
    @EnumSource(value = ApplicationStatus::class, names = ["ELIGIBLE", "APPROVED_WITH_CONDITIONS", "CONDITIONS_SUBMITTED"], mode = EnumSource.Mode.EXCLUDE)
    fun revertDecision(status: ApplicationStatus) {
        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns getStatusModelForStatus(status)
        assertThrows<DecisionReversionIsNotPossibleException> { approvedApplicationState.revertDecision() }
    }

    @ParameterizedTest(name = "get possible status to revert to {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["ELIGIBLE", "APPROVED_WITH_CONDITIONS", "CONDITIONS_SUBMITTED"])
    fun getPossibleStatusToRevertTo(status: ApplicationStatus) {
        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns getStatusModelForStatus(status)
        assertThat(approvedApplicationState.getPossibleStatusToRevertTo()).isEqualTo(status)
    }

    @ParameterizedTest(name = "getPossibleStatusToRevertTo {0} - invalid")
    @EnumSource(value = ApplicationStatus::class, names = ["ELIGIBLE", "APPROVED_WITH_CONDITIONS", "CONDITIONS_SUBMITTED"], mode = EnumSource.Mode.EXCLUDE)
    fun `getPossibleStatusToRevertTo - invalid`(status: ApplicationStatus) {
        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns getStatusModelForStatus(status)
        assertThat(approvedApplicationState.getPossibleStatusToRevertTo()).isNull()
    }

    @Test
    fun submit() {
        assertThrows<SubmitIsNotAllowedException> { approvedApplicationState.submit() }
    }

    @Test
    fun setAsEligible() {
        assertThrows<SetAsEligibleIsNotAllowedException> { approvedApplicationState.setAsEligible(actionInfo) }
    }

    @Test
    fun setAsIneligible() {
        assertThrows<SetAsIneligibleIsNotAllowedException> { approvedApplicationState.setAsIneligible(actionInfo) }
    }

    @Test
    fun approve() {
        assertThrows<ApproveIsNotAllowedException> { approvedApplicationState.approve(actionInfo) }
    }

    @Test
    fun approveWithConditions() {
        assertThrows<ApproveWithConditionsIsNotAllowedException> { approvedApplicationState.approveWithConditions(actionInfo) }
    }

    @Test
    fun refuse() {
        assertThrows<RefuseIsNotAllowedException> { approvedApplicationState.refuse(actionInfo) }
    }

}
