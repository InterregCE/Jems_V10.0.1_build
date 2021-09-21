package io.cloudflight.jems.server.project.service.application.workflow.states.first_step

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApproveIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.ApproveWithConditionsIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.DecisionReversionIsNotPossibleException
import io.cloudflight.jems.server.project.service.application.workflow.RefuseIsNotAllowedException
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
import org.springframework.context.ApplicationEventPublisher
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
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    private lateinit var approvedApplicationWithConditionsState: FirstStepApprovedApplicationWithConditionsState

    @BeforeEach
    fun setup() {
        every { projectSummary.id } returns PROJECT_ID
        every { projectSummary.status } returns ApplicationStatus.STEP1_APPROVED_WITH_CONDITIONS
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @Test
    fun `in first step decision cannot be updated to STEP1_APPROVED`() {
        assertThrows<ApproveIsNotAllowedException> { approvedApplicationWithConditionsState.approve(actionInfo) }
    }

    @Test
    fun `in first step decision cannot be updated to STEP1_NOT_APPROVED`() {
        assertThrows<RefuseIsNotAllowedException> { approvedApplicationWithConditionsState.refuse(actionInfo) }
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
