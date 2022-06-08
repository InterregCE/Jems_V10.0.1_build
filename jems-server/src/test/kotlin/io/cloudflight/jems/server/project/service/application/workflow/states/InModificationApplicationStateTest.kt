package io.cloudflight.jems.server.project.service.application.workflow.states

import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.InvalidPreviousStatusException
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.repository.user.toUserSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

class InModificationApplicationStateTest : UnitTest() {
    companion object {
        private const val PROJECT_ID = 1L
        private const val USER_ID = 7L
        const val CALL_ID = 10L

        private val projectCallSettings = ProjectCallSettings(
            callId = CALL_ID,
            callName = "dummy call",
            callType = CallType.STANDARD,
            startDate = ZonedDateTime.now().minusDays(1),
            endDate = ZonedDateTime.now().plusDays(1),
            endDateStep1 = null,
            lengthOfPeriod = 0,
            isAdditionalFundAllowed = false,
            flatRates = emptySet(),
            lumpSums = emptyList(),
            unitCosts = emptyList(),
            stateAids = emptyList(),
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = null,
            firstStepPreSubmissionCheckPluginKey = null
        )


        private val userEntity = UserEntity(
            id = USER_ID,
            email = "some@applicant",
            name = "",
            surname = "",
            userRole = UserRoleEntity(0, "role"),
            password = "",
            userStatus = UserStatus.ACTIVE
        )

        private val projectStatusContracted = ProjectStatus(
            id = 0L,
            updated = ZonedDateTime.now(),
            user = userEntity.toUserSummary(),
            status = ApplicationStatus.CONTRACTED
        )

        private val projectStatusModificationSubmitted = ProjectStatus(
            id = 0L,
            updated = ZonedDateTime.now(),
            user = userEntity.toUserSummary(),
            status = ApplicationStatus.MODIFICATION_SUBMITTED
        )

        private val projectStatusApproved = ProjectStatus(
            id = 0L,
            updated = ZonedDateTime.now(),
            user = userEntity.toUserSummary(),
            status = ApplicationStatus.APPROVED
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
    private lateinit var inModificationApplicationState: InModificationApplicationState

    @BeforeAll
    fun setup() {
        every { projectSummary.id } returns PROJECT_ID
        every { projectSummary.status } returns ApplicationStatus.DRAFT
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @Test
    fun `submit from CONTRACTED - successful`() {
        every { projectPersistence.getProjectCallSettings(InModificationApplicationStateTest.PROJECT_ID) } returns InModificationApplicationStateTest.projectCallSettings
        every { projectWorkflowPersistence.getApplicationPreviousStatus(any()) } returns projectStatusContracted
        every {
            projectWorkflowPersistence.updateProjectLastResubmission(
                any(),
                any(),
                ApplicationStatus.MODIFICATION_SUBMITTED
            )
        } returns ApplicationStatus.MODIFICATION_SUBMITTED


        Assertions.assertThat(inModificationApplicationState.submit())
            .isEqualTo(ApplicationStatus.MODIFICATION_SUBMITTED)
        verify(exactly = 1) {
            projectWorkflowPersistence.updateProjectLastResubmission(
                any(),
                any(),
                status = ApplicationStatus.MODIFICATION_SUBMITTED
            )
        }
    }

    @Test
    fun `submit from MODIFICATION_SUBMITTED - successful`() {
        every { projectPersistence.getProjectCallSettings(InModificationApplicationStateTest.PROJECT_ID) } returns InModificationApplicationStateTest.projectCallSettings
        every { projectWorkflowPersistence.getApplicationPreviousStatus(any()) } returns projectStatusModificationSubmitted
        every {
            projectWorkflowPersistence.updateProjectLastResubmission(
                any(),
                any(),
                projectStatusModificationSubmitted
            )
        } returns ApplicationStatus.MODIFICATION_SUBMITTED


        Assertions.assertThat(inModificationApplicationState.submit())
            .isEqualTo(ApplicationStatus.MODIFICATION_SUBMITTED)
        verify(exactly = 1) {
            projectWorkflowPersistence.updateProjectLastResubmission(
                any(),
                any(),
                status = projectStatusModificationSubmitted
            )
        }
    }

    @Test
    fun `submit throw InvalidPreviousStatusException`() {
        every { projectWorkflowPersistence.getApplicationPreviousStatus(any()) } returns projectStatusApproved

        assertThrows<InvalidPreviousStatusException> { inModificationApplicationState.submit() }
    }

}
