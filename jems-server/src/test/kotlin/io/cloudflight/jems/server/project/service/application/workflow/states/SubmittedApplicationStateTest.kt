package io.cloudflight.jems.server.project.service.application.workflow.states

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
import java.time.LocalDate

class SubmittedApplicationStateTest : UnitTest() {

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
    lateinit var auditService: AuditService

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    private lateinit var submittedApplicationState: SubmittedApplicationState

    @BeforeAll
    fun setup() {
        every { projectSummary.id } returns PROJECT_ID
        every { projectSummary.status } returns ApplicationStatus.SUBMITTED
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @Test
    fun returnToApplicant() {
        every { projectWorkflowPersistence.updateProjectCurrentStatus(any(), any(), any()) } returns ApplicationStatus.RETURNED_TO_APPLICANT
        assertThat(submittedApplicationState.returnToApplicant()).isEqualTo(ApplicationStatus.RETURNED_TO_APPLICANT)

        verify(exactly = 1) { projectWorkflowPersistence.updateProjectCurrentStatus(PROJECT_ID, USER_ID, ApplicationStatus.RETURNED_TO_APPLICANT) }
    }

    @ParameterizedTest
    @EnumSource(value = ApplicationStatus::class, names = ["ELIGIBLE", "INELIGIBLE"])
    fun `set as eligible or ineligible`(status: ApplicationStatus) {
        every { projectWorkflowPersistence.updateProjectEligibilityDecision(any(), any(), any(), any()) } returns status
        if (status == ApplicationStatus.ELIGIBLE)
            assertThat(submittedApplicationState.setAsEligible(actionInfo)).isEqualTo(status)
        else
            assertThat(submittedApplicationState.setAsIneligible(actionInfo)).isEqualTo(status)

        verify(exactly = 1) {
            projectWorkflowPersistence.updateProjectEligibilityDecision(PROJECT_ID, USER_ID, status, actionInfo) }
    }

    @Test
    fun submit() {
        assertThrows<SubmitIsNotAllowedException> { submittedApplicationState.submit() }
    }

    @Test
    fun approve() {
        assertThrows<ApproveIsNotAllowedException> { submittedApplicationState.approve(actionInfo) }
    }

    @Test
    fun approveWithConditions() {
        assertThrows<ApproveWithConditionsIsNotAllowedException> {
            submittedApplicationState.approveWithConditions(actionInfo)
        }
    }

    @Test
    fun revertDecision() {
        assertThrows<RevertLastActionOnApplicationIsNotAllowedException> { submittedApplicationState.revertDecision() }
    }

    @Test
    fun refuse() {
        assertThrows<RefuseIsNotAllowedException> { submittedApplicationState.refuse(actionInfo) }
    }

    @Test
    fun getPossibleStatusToRevertTo() {
        assertThat(submittedApplicationState.getPossibleStatusToRevertTo()).isNull()
    }

}
