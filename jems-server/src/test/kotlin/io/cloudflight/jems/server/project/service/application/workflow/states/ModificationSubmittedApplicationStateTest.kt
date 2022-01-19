package io.cloudflight.jems.server.project.service.application.workflow.states

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.authorization.ProjectAuthorization
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDate

class ModificationSubmittedApplicationStateTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val USER_ID = 7L
        const val CALL_ID = 10L


        private val actionInfo = ApplicationActionInfo(
            note = "some dummy note",
            date = LocalDate.now(),
            entryIntoForceDate = LocalDate.now()
        )

    }

    @MockK
    lateinit var applicationActionInfo: ApplicationActionInfo

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

    @MockK
    lateinit var projectAuthorization: ProjectAuthorization

    @InjectMockKs
    private lateinit var modificationSubmittedApplicationState: ModificationSubmittedApplicationState

    @BeforeAll
    fun setup() {
        every { projectSummary.id } returns PROJECT_ID
        every { projectSummary.status } returns ApplicationStatus.DRAFT
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @Test
    fun `approveModification - success`() {
        every {
            projectWorkflowPersistence.updateProjectModificationDecision(
                any(),
                any(),
                ApplicationStatus.CONTRACTED,
                any()
            )
        } returns ApplicationStatus.CONTRACTED

        Assertions.assertThat(modificationSubmittedApplicationState.approveModification(actionInfo))
            .isEqualTo(ApplicationStatus.CONTRACTED)
    }

    @Test
    fun `handBack application - success`() {
        every {
            projectAuthorization.hasPermission(
                any(),
                any()
            )
        } returns true
        every {
            projectWorkflowPersistence.updateProjectCurrentStatus(
                any(),
                any(),
                any()
            )
        } returns ApplicationStatus.IN_MODIFICATION

        Assertions.assertThat(modificationSubmittedApplicationState.handBackToApplicant())
            .isEqualTo(ApplicationStatus.IN_MODIFICATION)
    }

    @Test
    fun `reject modifications - success`() {
        every {
            projectWorkflowPersistence.updateProjectModificationDecision(
                any(),
                any(),
                any(),
                actionInfo
            )
        } returns ApplicationStatus.CONTRACTED
        every {
            projectWorkflowPersistence.restoreProjectToLastVersionByStatus(
                any(),
                any()
            )
        } returns Unit
        every {
            projectWorkflowPersistence.updateProjectCurrentStatus(
                any(),
                any(),
                any()
            )
        } returns ApplicationStatus.CONTRACTED

        Assertions.assertThat(modificationSubmittedApplicationState.rejectModification(actionInfo))
            .isEqualTo(ApplicationStatus.CONTRACTED)
    }

}
