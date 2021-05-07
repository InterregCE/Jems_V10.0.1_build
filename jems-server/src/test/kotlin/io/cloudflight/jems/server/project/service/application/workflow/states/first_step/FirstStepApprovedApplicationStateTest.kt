package io.cloudflight.jems.server.project.service.application.workflow.states.first_step

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.service.SecurityService
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
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import java.time.LocalDate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

@ExtendWith(MockKExtension::class)
class FirstStepApprovedApplicationStateTest {

    companion object {
        private const val PROJECT_ID = 8L
        private const val USER_ID = 10L

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
    private lateinit var approvedApplicationState: FirstStepApprovedApplicationState

    @BeforeEach
    fun setup() {
        every { projectSummary.id }.returnsMany(PROJECT_ID)
        every { projectSummary.status }.returnsMany(ApplicationStatus.STEP1_APPROVED)
        every { securityService.getUserIdOrThrow() }.returnsMany(USER_ID)
    }

    @Test
    fun `should have state APPROVED_WITH_CONDITIONS when reverting decision`() {
        val status = ApplicationStatus.STEP1_APPROVED_WITH_CONDITIONS
        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns status
        every { projectWorkflowPersistence.revertCurrentStatusToPreviousStatus(PROJECT_ID) } returns status
        every { projectWorkflowPersistence.resetProjectFundingDecisionToCurrentStatus(PROJECT_ID) } returns status

        assertThat(approvedApplicationState.revertDecision()).isEqualTo(status)
        verify(exactly = 1) { projectWorkflowPersistence.revertCurrentStatusToPreviousStatus(PROJECT_ID) }
        verify(exactly = 1) { projectWorkflowPersistence.resetProjectFundingDecisionToCurrentStatus(PROJECT_ID) }
    }

    @Test
    fun `should have state ELIGIBLE when reverting decision`() {
        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns ApplicationStatus.STEP1_ELIGIBLE
        every { projectWorkflowPersistence.revertCurrentStatusToPreviousStatus(PROJECT_ID) } returns ApplicationStatus.STEP1_ELIGIBLE
        every { projectWorkflowPersistence.clearProjectFundingDecision(PROJECT_ID) } answers { }

        assertThat(approvedApplicationState.revertDecision()).isEqualTo(ApplicationStatus.STEP1_ELIGIBLE)
        verify(exactly = 1) { projectWorkflowPersistence.revertCurrentStatusToPreviousStatus(PROJECT_ID) }
        verify(exactly = 1) { projectWorkflowPersistence.clearProjectFundingDecision(PROJECT_ID) }
    }

    @ParameterizedTest(name = "revertDecision to {0} - invalid")
    @EnumSource(
        value = ApplicationStatus::class,
        names = ["STEP1_ELIGIBLE", "STEP1_APPROVED_WITH_CONDITIONS"],
        mode = EnumSource.Mode.EXCLUDE
    )
    fun `should throw DecisionReversionIsNotPossibleException when reverting decision`(status: ApplicationStatus) {
        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns status
        assertThrows<DecisionReversionIsNotPossibleException> { approvedApplicationState.revertDecision() }
    }

    @ParameterizedTest(name = "get possible status to revert to {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["STEP1_ELIGIBLE", "STEP1_APPROVED_WITH_CONDITIONS"])
    fun `should get possible status when reverting decision`(status: ApplicationStatus) {
        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns status
        assertThat(approvedApplicationState.getPossibleStatusToRevertTo()).isEqualTo(status)
    }

    @ParameterizedTest(name = "getPossibleStatusToRevertTo {0} - invalid")
    @EnumSource(
        value = ApplicationStatus::class,
        names = ["STEP1_ELIGIBLE", "STEP1_APPROVED_WITH_CONDITIONS"],
        mode = EnumSource.Mode.EXCLUDE
    )
    fun `should get invalid possible status when reverting decision`(status: ApplicationStatus) {
        every { projectWorkflowPersistence.getApplicationPreviousStatus(PROJECT_ID) } returns status
        assertThat(approvedApplicationState.getPossibleStatusToRevertTo()).isNull()
    }

    @Test
    fun `should throw ReturnToApplicantIsNotAllowedException when returning application to applicant`() {
        assertThrows<ReturnToApplicantIsNotAllowedException> { approvedApplicationState.returnToApplicant() }
    }

    @Test
    fun `should throw SubmitIsNotAllowedException when submitting application`() {
        assertThrows<SubmitIsNotAllowedException> { approvedApplicationState.submit() }
    }

    @Test
    fun `should throw SetAsEligibleIsNotAllowedException when setting application as ELIGIBLE`() {
        assertThrows<SetAsEligibleIsNotAllowedException> { approvedApplicationState.setAsEligible(actionInfo) }
    }

    @Test
    fun `should throw SetAsIneligibleIsNotAllowedException when setting application as INELIGIBLE`() {
        assertThrows<SetAsIneligibleIsNotAllowedException> { approvedApplicationState.setAsIneligible(actionInfo) }
    }

    @Test
    fun `should throw ApproveIsNotAllowedException when approving application`() {
        assertThrows<ApproveIsNotAllowedException> { approvedApplicationState.approve(actionInfo) }
    }

    @Test
    fun `should throw ApproveWithConditionsIsNotAllowedException when approving application with conditions`() {
        assertThrows<ApproveWithConditionsIsNotAllowedException> {
            approvedApplicationState.approveWithConditions(
                actionInfo
            )
        }
    }

    @Test
    fun `should throw RefuseIsNotAllowedException when refusing application`() {
        assertThrows<RefuseIsNotAllowedException> { approvedApplicationState.refuse(actionInfo) }
    }

}
