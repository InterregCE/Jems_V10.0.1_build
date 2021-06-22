package io.cloudflight.jems.server.project.service.application.workflow.states

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.service.model.ApplicationFormConfiguration
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
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDate
import java.time.ZonedDateTime

class DraftApplicationStateTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val USER_ID = 7L
        private const val CALL_ID = 10L

        private val projectCallSettings = ProjectCallSettings(
            callId = CALL_ID,
            callName = "dummy call",
            startDate = ZonedDateTime.now().minusDays(1),
            endDate = ZonedDateTime.now().plusDays(1),
            endDateStep1 = null,
            lengthOfPeriod = 0,
            isAdditionalFundAllowed = false,
            flatRates = emptySet(),
            lumpSums = emptyList(),
            unitCosts = emptyList(),
            applicationFormConfiguration= ApplicationFormConfiguration(1,"test configuration", mutableSetOf())
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
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    private lateinit var draftApplicationState: DraftApplicationState

    @BeforeAll
    fun setup() {
        every { projectSummary.id } returns PROJECT_ID
        every { projectSummary.status } returns ApplicationStatus.DRAFT
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @Test
    fun `submit - successful`() {
        every { projectPersistence.getProjectCallSettings(PROJECT_ID) } returns projectCallSettings
        every {
            projectWorkflowPersistence.updateApplicationFirstSubmission(any(), any(), any())
        } returns ApplicationStatus.SUBMITTED

        assertThat(draftApplicationState.submit()).isEqualTo(ApplicationStatus.SUBMITTED)
        verify(exactly = 1) {
            projectWorkflowPersistence.updateApplicationFirstSubmission(
                PROJECT_ID,
                USER_ID,
                ApplicationStatus.SUBMITTED
            )
        }
    }

    @Test
    fun `submit - call is not yet open`() {
        every { projectPersistence.getProjectCallSettings(PROJECT_ID) } returns projectCallSettings.copy(
            startDate = ZonedDateTime.now().plusDays(1)
        )
        assertThrows<CallIsNotOpenException> { draftApplicationState.submit() }
    }

    @Test
    fun `submit - call is already closed`() {
        every { projectPersistence.getProjectCallSettings(PROJECT_ID) } returns projectCallSettings.copy(
            endDate = ZonedDateTime.now().minusDays(1)
        )
        assertThrows<CallIsNotOpenException> { draftApplicationState.submit() }
    }

    @Test
    fun setAsEligible() {
        assertThrows<SetAsEligibleIsNotAllowedException> { draftApplicationState.setAsEligible(actionInfo) }
    }

    @Test
    fun setAsIneligible() {
        assertThrows<SetAsIneligibleIsNotAllowedException> { draftApplicationState.setAsIneligible(actionInfo) }
    }

    @Test
    fun approve() {
        assertThrows<ApproveIsNotAllowedException> { draftApplicationState.approve(actionInfo) }
    }

    @Test
    fun approveWithConditions() {
        assertThrows<ApproveWithConditionsIsNotAllowedException> {
            draftApplicationState.approveWithConditions(
                actionInfo
            )
        }
    }

    @Test
    fun returnToApplicant() {
        assertThrows<ReturnToApplicantIsNotAllowedException> { draftApplicationState.returnToApplicant() }
    }

    @Test
    fun revertDecision() {
        assertThrows<RevertLastActionOnApplicationIsNotAllowedException> { draftApplicationState.revertDecision() }
    }

    @Test
    fun refuse() {
        assertThrows<RefuseIsNotAllowedException> { draftApplicationState.refuse(actionInfo) }
    }

    @Test
    fun getPossibleStatusToRevertTo() {
        assertThat(draftApplicationState.getPossibleStatusToRevertTo()).isNull()
    }

}
