package io.cloudflight.jems.server.project.service.application.workflow.states

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApproveWithConditionsIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.DecisionReversionIsNotPossibleException
import io.cloudflight.jems.server.project.service.application.workflow.FundingDecisionIsBeforeEligibilityDecisionException
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
class ApprovedApplicationWithConditionsStateTest {

    companion object {
        private const val PROJECT_ID = 14L
        private const val USER_ID = 12L

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
    private lateinit var approvedApplicationWithConditionsState: ApprovedApplicationWithConditionsState

    @BeforeEach
    fun setup() {
        every { projectSummary.id } returns PROJECT_ID
        every { projectSummary.status } returns ApplicationStatus.APPROVED_WITH_CONDITIONS
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @Test
    fun approve() {
        every { projectWorkflowPersistence.getProjectEligibilityDecisionDate(PROJECT_ID) } returns LocalDate.now().minusDays(1)
        every { projectWorkflowPersistence.updateProjectFundingDecision(PROJECT_ID, USER_ID, ApplicationStatus.APPROVED, any()) } returns ApplicationStatus.APPROVED

        assertThat(approvedApplicationWithConditionsState.approve(actionInfo)).isEqualTo(ApplicationStatus.APPROVED)
        verify(exactly = 1) { projectWorkflowPersistence.updateProjectFundingDecision(PROJECT_ID, USER_ID, ApplicationStatus.APPROVED, actionInfo)  }
    }

    @Test
    fun `approve - invalid funding date`() {
        every { projectWorkflowPersistence.getProjectEligibilityDecisionDate(PROJECT_ID) } returns LocalDate.now().minusDays(1)
        assertThrows<FundingDecisionIsBeforeEligibilityDecisionException> {
            approvedApplicationWithConditionsState.approve(actionInfo.copy(date = null))
        }
    }

    @Test
    fun `approve - invalid decision date`() {
        every { projectWorkflowPersistence.getProjectEligibilityDecisionDate(PROJECT_ID) } returns null
        assertThrows<FundingDecisionIsBeforeEligibilityDecisionException> {
            approvedApplicationWithConditionsState.approve(actionInfo)
        }
    }

    @Test
    fun `approve - funding date after decision date`() {
        every { projectWorkflowPersistence.getProjectEligibilityDecisionDate(PROJECT_ID) } returns LocalDate.now().plusDays(1)
        assertThrows<FundingDecisionIsBeforeEligibilityDecisionException> {
            approvedApplicationWithConditionsState.approve(actionInfo)
        }
    }

    @Test
    fun refuse() {
        every { projectWorkflowPersistence.getProjectEligibilityDecisionDate(PROJECT_ID) } returns LocalDate.now().minusDays(1)
        every { projectWorkflowPersistence.updateProjectFundingDecision(PROJECT_ID, USER_ID, ApplicationStatus.NOT_APPROVED, any()) } returns ApplicationStatus.NOT_APPROVED

        assertThat(approvedApplicationWithConditionsState.refuse(actionInfo)).isEqualTo(ApplicationStatus.NOT_APPROVED)
        verify(exactly = 1) { projectWorkflowPersistence.updateProjectFundingDecision(PROJECT_ID, USER_ID, ApplicationStatus.NOT_APPROVED, actionInfo)  }
    }

    @Test
    fun `refuse - invalid funding date`() {
        every { projectWorkflowPersistence.getProjectEligibilityDecisionDate(PROJECT_ID) } returns LocalDate.now().minusDays(1)
        assertThrows<FundingDecisionIsBeforeEligibilityDecisionException> {
            approvedApplicationWithConditionsState.refuse(actionInfo.copy(date = null))
        }
    }

    @Test
    fun `refuse - invalid decision date`() {
        every { projectWorkflowPersistence.getProjectEligibilityDecisionDate(PROJECT_ID) } returns null
        assertThrows<FundingDecisionIsBeforeEligibilityDecisionException> {
            approvedApplicationWithConditionsState.refuse(actionInfo)
        }
    }

    @Test
    fun `refuse - funding date after decision date`() {
        every { projectWorkflowPersistence.getProjectEligibilityDecisionDate(PROJECT_ID) } returns LocalDate.now().plusDays(1)
        assertThrows<FundingDecisionIsBeforeEligibilityDecisionException> {
            approvedApplicationWithConditionsState.refuse(actionInfo)
        }
    }

    @Test
    fun returnToApplicant() {
        every { projectWorkflowPersistence.updateProjectCurrentStatus(any(), any(), any()) } returns ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS
        assertThat(approvedApplicationWithConditionsState.returnToApplicant()).isEqualTo(ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS)

        verify(exactly = 1) {
            projectWorkflowPersistence.updateProjectCurrentStatus(PROJECT_ID, USER_ID, ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS)
        }
    }

    @Test
    fun `revertDecision to ELIGIBLE`() {
        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns getStatusModelForStatus(ApplicationStatus.ELIGIBLE)
        every { projectWorkflowPersistence.revertCurrentStatusToPreviousStatus(PROJECT_ID) } returns ApplicationStatus.ELIGIBLE
        every { projectWorkflowPersistence.clearProjectFundingDecision(PROJECT_ID) } answers { }

        assertThat(approvedApplicationWithConditionsState.revertDecision()).isEqualTo(ApplicationStatus.ELIGIBLE)
        verify(exactly = 1) { projectWorkflowPersistence.revertCurrentStatusToPreviousStatus(PROJECT_ID)  }
        verify(exactly = 1) { projectWorkflowPersistence.clearProjectFundingDecision(PROJECT_ID)  }
    }

    @ParameterizedTest(name = "revertDecision to {0} - invalid")
    @EnumSource(value = ApplicationStatus::class, names = ["ELIGIBLE"], mode = EnumSource.Mode.EXCLUDE)
    fun revertDecision(status: ApplicationStatus) {
        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns getStatusModelForStatus(status)
        every { projectWorkflowPersistence.clearProjectFundingDecision(PROJECT_ID)} returns Unit
        assertThrows<DecisionReversionIsNotPossibleException> {
            approvedApplicationWithConditionsState.revertDecision()
        }
    }

    @ParameterizedTest(name = "get possible status to revert to {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["ELIGIBLE"])
    fun getPossibleStatusToRevertTo(status: ApplicationStatus) {
        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns getStatusModelForStatus(status)
        assertThat(approvedApplicationWithConditionsState.getPossibleStatusToRevertTo()).isEqualTo(status)
    }

    @ParameterizedTest(name = "getPossibleStatusToRevertTo {0} - invalid")
    @EnumSource(value = ApplicationStatus::class, names = ["ELIGIBLE"], mode = EnumSource.Mode.EXCLUDE)
    fun `getPossibleStatusToRevertTo - invalid`(status: ApplicationStatus) {
        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns getStatusModelForStatus(status)
        assertThat(approvedApplicationWithConditionsState.getPossibleStatusToRevertTo()).isNull()
    }

    @Test
    fun submit() {
        assertThrows<SubmitIsNotAllowedException> { approvedApplicationWithConditionsState.submit() }
    }

    @Test
    fun setAsEligible() {
        assertThrows<SetAsEligibleIsNotAllowedException> {
            approvedApplicationWithConditionsState.setAsEligible(actionInfo)
        }
    }

    @Test
    fun setAsIneligible() {
        assertThrows<SetAsIneligibleIsNotAllowedException> {
            approvedApplicationWithConditionsState.setAsIneligible(actionInfo)
        }
    }

    @Test
    fun approveWithConditions() {
        assertThrows<ApproveWithConditionsIsNotAllowedException> {
            approvedApplicationWithConditionsState.approveWithConditions(actionInfo)
        }
    }

//    @Test
//    fun `revertDecision to APPROVED_WITH_CONDITIONS`() {
//        val status = ApplicationStatus.APPROVED_WITH_CONDITIONS
//        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns status
//        every { projectWorkflowPersistence.revertCurrentStatusToPreviousStatus(PROJECT_ID) } returns status
//        every { projectWorkflowPersistence.resetProjectFundingDecisionToCurrentStatus(PROJECT_ID) } returns status
//
//        assertThat(approvedApplicationState.revertDecision()).isEqualTo(status)
//        verify(exactly = 1) { projectWorkflowPersistence.revertCurrentStatusToPreviousStatus(PROJECT_ID)  }
//        verify(exactly = 1) { projectWorkflowPersistence.resetProjectFundingDecisionToCurrentStatus(PROJECT_ID)  }
//    }
//
//    @Test
//    fun `revertDecision to ELIGIBLE`() {
//        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns ApplicationStatus.ELIGIBLE
//        every { projectWorkflowPersistence.revertCurrentStatusToPreviousStatus(PROJECT_ID) } returns ApplicationStatus.ELIGIBLE
//        every { projectWorkflowPersistence.clearProjectFundingDecision(PROJECT_ID) } answers { }
//
//        assertThat(approvedApplicationState.revertDecision()).isEqualTo(ApplicationStatus.ELIGIBLE)
//        verify(exactly = 1) { projectWorkflowPersistence.revertCurrentStatusToPreviousStatus(PROJECT_ID)  }
//        verify(exactly = 1) { projectWorkflowPersistence.clearProjectFundingDecision(PROJECT_ID)  }
//    }
//
//    @ParameterizedTest(name = "revertDecision to {0} - invalid")
//    @EnumSource(value = ApplicationStatus::class, names = ["ELIGIBLE", "APPROVED_WITH_CONDITIONS"], mode = EnumSource.Mode.EXCLUDE)
//    fun revertDecision(status: ApplicationStatus) {
//        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns status
//        assertThrows<DecisionReversionIsNotPossibleException> { approvedApplicationState.revertDecision() }
//    }
//
//    @ParameterizedTest(name = "get possible status to revert to {0}")
//    @EnumSource(value = ApplicationStatus::class, names = ["ELIGIBLE", "APPROVED_WITH_CONDITIONS"])
//    fun getPossibleStatusToRevertTo(status: ApplicationStatus) {
//        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns status
//        assertThat(approvedApplicationState.getPossibleStatusToRevertTo()).isEqualTo(status)
//    }
//
//    @ParameterizedTest(name = "getPossibleStatusToRevertTo {0} - invalid")
//    @EnumSource(value = ApplicationStatus::class, names = ["ELIGIBLE", "APPROVED_WITH_CONDITIONS"], mode = EnumSource.Mode.EXCLUDE)
//    fun `getPossibleStatusToRevertTo - invalid`(status: ApplicationStatus) {
//        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns status
//        assertThat(approvedApplicationState.getPossibleStatusToRevertTo()).isNull()
//    }
//
//    @Test
//    fun submit() {
//        assertThrows<SubmitIsNotAllowedException> { approvedApplicationState.submit() }
//    }
//
//    @Test
//    fun setAsEligible() {
//        assertThrows<SetAsEligibleIsNotAllowedException> { approvedApplicationState.setAsEligible(actionInfo) }
//    }
//
//    @Test
//    fun setAsIneligible() {
//        assertThrows<SetAsIneligibleIsNotAllowedException> { approvedApplicationState.setAsIneligible(actionInfo) }
//    }
//
//    @Test
//    fun approve() {
//        assertThrows<ApproveIsNotAllowedException> { approvedApplicationState.approve(actionInfo) }
//    }
//
//    @Test
//    fun approveWithConditions() {
//        assertThrows<ApproveWithConditionsIsNotAllowedException> { approvedApplicationState.approveWithConditions(actionInfo) }
//    }
//
//    @Test
//    fun refuse() {
//        assertThrows<RefuseIsNotAllowedException> { approvedApplicationState.refuse(actionInfo) }
//    }

}
