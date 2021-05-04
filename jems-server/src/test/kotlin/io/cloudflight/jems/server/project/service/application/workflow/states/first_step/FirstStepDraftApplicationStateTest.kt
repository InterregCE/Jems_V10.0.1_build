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
import io.cloudflight.jems.server.project.service.application.workflow.CallIsNotOpenException
import io.cloudflight.jems.server.project.service.application.workflow.RefuseIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.ReturnToApplicantIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.RevertLastActionOnApplicationIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.SetAsEligibleIsNotAllowedException
import io.cloudflight.jems.server.project.service.application.workflow.SetAsIneligibleIsNotAllowedException
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
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
import java.time.LocalDate
import java.time.ZonedDateTime

class FirstStepDraftApplicationStateTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val USER_ID = 7L
        private const val CALL_ID = 10L

        private val projectCallSettings = ProjectCallSettings(
            callId = CALL_ID,
            callName = "dummy call",
            startDate = ZonedDateTime.now().minusDays(1),
            endDate = ZonedDateTime.now().plusDays(1),
            endDateStep1 = ZonedDateTime.now().plusHours(1),
            lengthOfPeriod = 0,
            isAdditionalFundAllowed = false,
            flatRates = emptySet(),
            lumpSums = emptyList(),
            unitCosts = emptyList(),
        )

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

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    private lateinit var draftApplicationState: FirstStepDraftApplicationState

    @BeforeAll
    fun setup() {
        every { projectSummary.id } returns PROJECT_ID
        every { projectSummary.status } returns ApplicationStatus.STEP1_DRAFT
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @Test
    fun `should have state STEP1_SUBMITTED when submitting application`() {
        every { projectPersistence.getProjectCallSettings(PROJECT_ID) } returns projectCallSettings
        every {
            projectWorkflowPersistence.updateApplicationFirstSubmission(any(), any(), any())
        } returns ApplicationStatus.STEP1_SUBMITTED

        assertThat(draftApplicationState.submit()).isEqualTo(ApplicationStatus.STEP1_SUBMITTED)
        verify(exactly = 1) {
            projectWorkflowPersistence.updateApplicationFirstSubmission(
                PROJECT_ID,
                USER_ID,
                ApplicationStatus.STEP1_SUBMITTED
            )
        }
    }

    @Test
    fun `should throw CallIsNotOpenException when submitting and call is not yet open`() {
        every { projectPersistence.getProjectCallSettings(PROJECT_ID) } returns projectCallSettings.copy(
            startDate = ZonedDateTime.now().plusDays(1)
        )
        assertThrows<CallIsNotOpenException> { draftApplicationState.submit() }
    }

    @Test
    fun `should throw CallIsNotOpenException when submitting and call is already closed`() {
        every { projectPersistence.getProjectCallSettings(PROJECT_ID) } returns projectCallSettings.copy(
            endDateStep1 = ZonedDateTime.now().minusDays(1)
        )
        assertThrows<CallIsNotOpenException> { draftApplicationState.submit() }
    }

    @Test
    fun `should throw SetAsEligibleIsNotAllowedException when setting application as eligible`() {
        assertThrows<SetAsEligibleIsNotAllowedException> { draftApplicationState.setAsEligible(actionInfo) }
    }

    @Test
    fun `should throw SetAsIneligibleIsNotAllowedException when setting application as ineligible`() {
        assertThrows<SetAsIneligibleIsNotAllowedException> { draftApplicationState.setAsIneligible(actionInfo) }
    }

    @Test
    fun `should throw ApproveIsNotAllowedException when approving application`() {
        assertThrows<ApproveIsNotAllowedException> { draftApplicationState.approve(actionInfo) }
    }

    @Test
    fun `should throw ApproveWithConditionsIsNotAllowedException when approving application with conditions`() {
        assertThrows<ApproveWithConditionsIsNotAllowedException> {
            draftApplicationState.approveWithConditions(
                actionInfo
            )
        }
    }

    @Test
    fun `should throw ReturnToApplicantIsNotAllowedException when returning application to applicant`() {
        assertThrows<ReturnToApplicantIsNotAllowedException> { draftApplicationState.returnToApplicant() }
    }

    @Test
    fun `should throw RevertLastActionOnApplicationIsNotAllowedException when reverting decision`() {
        assertThrows<RevertLastActionOnApplicationIsNotAllowedException> { draftApplicationState.revertDecision() }
    }

    @Test
    fun `should throw RefuseIsNotAllowedException when refusing application`() {
        assertThrows<RefuseIsNotAllowedException> { draftApplicationState.refuse(actionInfo) }
    }

    @Test
    fun `should get possible status when reverting decision`() {
        assertThat(draftApplicationState.getPossibleStatusToRevertTo()).isNull()
    }

}
