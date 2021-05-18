package io.cloudflight.jems.server.project.service.application.workflow.states.first_step

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApproveWithConditionsIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.DecisionReversionIsNotPossibleException
import io.cloudflight.jems.server.project.service.application.workflow.FundingDecisionIsBeforeEligibilityDecisionException
import io.cloudflight.jems.server.project.service.application.workflow.ReturnToApplicantIsNotAllowedException
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
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class FirstStepApprovedApplicationWithConditionsStateTest {

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
    lateinit var auditService: AuditService

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    private lateinit var approvedApplicationWithConditionsState: FirstStepApprovedApplicationWithConditionsState

    @BeforeEach
    fun setup() {
        every { projectSummary.id } returns PROJECT_ID
        every { projectSummary.status } returns ApplicationStatus.STEP1_APPROVED_WITH_CONDITIONS
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @Test
    fun `should have state STEP1_APPROVED when approving application`() {
        every { projectWorkflowPersistence.getProjectEligibilityDecisionDate(PROJECT_ID) } returns LocalDate.now()
            .minusDays(1)
        every {
            projectWorkflowPersistence.updateProjectCurrentStatus(
                PROJECT_ID,
                USER_ID,
                ApplicationStatus.STEP1_APPROVED,
                any()
            )
        } returns ApplicationStatus.STEP1_APPROVED

        assertThat(approvedApplicationWithConditionsState.approve(actionInfo)).isEqualTo(ApplicationStatus.STEP1_APPROVED)
        verify(exactly = 1) {
            projectWorkflowPersistence.updateProjectCurrentStatus(
                PROJECT_ID,
                USER_ID,
                ApplicationStatus.STEP1_APPROVED,
                actionInfo
            )
        }
    }

    @Test
    fun `should throw FundingDecisionIsBeforeEligibilityDecisionException when approving application with invalid funding date`() {
        every { projectWorkflowPersistence.getProjectEligibilityDecisionDate(PROJECT_ID) } returns LocalDate.now()
            .minusDays(1)
        assertThrows<FundingDecisionIsBeforeEligibilityDecisionException> {
            approvedApplicationWithConditionsState.approve(actionInfo.copy(date = null))
        }
    }

    @Test
    fun `should throw FundingDecisionIsBeforeEligibilityDecisionException when approving application with invalid decision date`() {
        every { projectWorkflowPersistence.getProjectEligibilityDecisionDate(PROJECT_ID) } returns null
        assertThrows<FundingDecisionIsBeforeEligibilityDecisionException> {
            approvedApplicationWithConditionsState.approve(actionInfo)
        }
    }

    @Test
    fun `should throw FundingDecisionIsBeforeEligibilityDecisionException with funding date after decision date`() {
        every { projectWorkflowPersistence.getProjectEligibilityDecisionDate(PROJECT_ID) } returns LocalDate.now()
            .plusDays(1)
        assertThrows<FundingDecisionIsBeforeEligibilityDecisionException> {
            approvedApplicationWithConditionsState.approve(actionInfo)
        }
    }

    @Test
    fun `should have state STEP1_NOT_APPROVED when approving application`() {
        every { projectWorkflowPersistence.getProjectEligibilityDecisionDate(PROJECT_ID) } returns LocalDate.now()
            .minusDays(1)
        every {
            projectWorkflowPersistence.updateProjectCurrentStatus(
                PROJECT_ID,
                USER_ID,
                ApplicationStatus.STEP1_NOT_APPROVED,
                any()
            )
        } returns ApplicationStatus.STEP1_NOT_APPROVED

        assertThat(approvedApplicationWithConditionsState.refuse(actionInfo)).isEqualTo(ApplicationStatus.STEP1_NOT_APPROVED)
        verify(exactly = 1) {
            projectWorkflowPersistence.updateProjectCurrentStatus(
                PROJECT_ID,
                USER_ID,
                ApplicationStatus.STEP1_NOT_APPROVED,
                actionInfo
            )
        }
    }

    @Test
    fun `should throw FundingDecisionIsBeforeEligibilityDecisionException when refusing with invalid funding date`() {
        every { projectWorkflowPersistence.getProjectEligibilityDecisionDate(PROJECT_ID) } returns LocalDate.now()
            .minusDays(1)
        assertThrows<FundingDecisionIsBeforeEligibilityDecisionException> {
            approvedApplicationWithConditionsState.refuse(actionInfo.copy(date = null))
        }
    }

    @Test
    fun `should throw FundingDecisionIsBeforeEligibilityDecisionException when refusing with invalid decision date`() {
        every { projectWorkflowPersistence.getProjectEligibilityDecisionDate(PROJECT_ID) } returns null
        assertThrows<FundingDecisionIsBeforeEligibilityDecisionException> {
            approvedApplicationWithConditionsState.refuse(actionInfo)
        }
    }

    @Test
    fun `should throw FundingDecisionIsBeforeEligibilityDecisionException when refusing with funding date after decision date`() {
        every { projectWorkflowPersistence.getProjectEligibilityDecisionDate(PROJECT_ID) } returns LocalDate.now()
            .plusDays(1)
        assertThrows<FundingDecisionIsBeforeEligibilityDecisionException> {
            approvedApplicationWithConditionsState.refuse(actionInfo)
        }
    }

    @Test
    fun `should throw exception when returning application to applicant`() {
        assertThrows<ReturnToApplicantIsNotAllowedException> { approvedApplicationWithConditionsState.returnToApplicant() }
    }

    @Test
    fun `should have state STEP1_ELIGIBLE when reverting decision`() {
        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns getStatusModelForStatus(ApplicationStatus.STEP1_ELIGIBLE)
        every { projectWorkflowPersistence.revertCurrentStatusToPreviousStatus(PROJECT_ID) } returns ApplicationStatus.STEP1_ELIGIBLE
        every { projectWorkflowPersistence.clearProjectFundingDecision(PROJECT_ID) } answers { }

        assertThat(approvedApplicationWithConditionsState.revertDecision()).isEqualTo(ApplicationStatus.STEP1_ELIGIBLE)
        verify(exactly = 1) { projectWorkflowPersistence.revertCurrentStatusToPreviousStatus(PROJECT_ID) }
        verify(exactly = 1) { projectWorkflowPersistence.clearProjectFundingDecision(PROJECT_ID) }
    }

    @ParameterizedTest(name = "revertDecision to {0} - invalid")
    @EnumSource(value = ApplicationStatus::class, names = ["STEP1_ELIGIBLE"], mode = EnumSource.Mode.EXCLUDE)
    fun `should throw DecisionReversionIsNotPossibleException when reverting decision`(status: ApplicationStatus) {
        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns getStatusModelForStatus(status)
        assertThrows<DecisionReversionIsNotPossibleException> {
            approvedApplicationWithConditionsState.revertDecision()
        }
    }

    @ParameterizedTest(name = "get possible status to revert to {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["STEP1_ELIGIBLE"])
    fun `should get possible status when reverting decision`(status: ApplicationStatus) {
        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns getStatusModelForStatus(status)
        assertThat(approvedApplicationWithConditionsState.getPossibleStatusToRevertTo()).isEqualTo(status)
    }

    @ParameterizedTest(name = "getPossibleStatusToRevertTo {0} - invalid")
    @EnumSource(value = ApplicationStatus::class, names = ["STEP1_ELIGIBLE"], mode = EnumSource.Mode.EXCLUDE)
    fun `should get invalid possible status when reverting decision`(status: ApplicationStatus) {
        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns getStatusModelForStatus(status)
        assertThat(approvedApplicationWithConditionsState.getPossibleStatusToRevertTo()).isNull()
    }

    @Test
    fun `should throw SubmitIsNotAllowedException when submitting application`() {
        assertThrows<SubmitIsNotAllowedException> { approvedApplicationWithConditionsState.submit() }
    }

    @Test
    fun `should throw SetAsEligibleIsNotAllowedException when setting application as eligible`() {
        assertThrows<SetAsEligibleIsNotAllowedException> {
            approvedApplicationWithConditionsState.setAsEligible(actionInfo)
        }
    }

    @Test
    fun `should throw SetAsIneligibleIsNotAllowedException when setting application to ineligible`() {
        assertThrows<SetAsIneligibleIsNotAllowedException> {
            approvedApplicationWithConditionsState.setAsIneligible(actionInfo)
        }
    }

    @Test
    fun `should throw ApproveWithConditionsIsNotAllowedException when approving application with conditions`() {
        assertThrows<ApproveWithConditionsIsNotAllowedException> {
            approvedApplicationWithConditionsState.approveWithConditions(actionInfo)
        }
    }

}
