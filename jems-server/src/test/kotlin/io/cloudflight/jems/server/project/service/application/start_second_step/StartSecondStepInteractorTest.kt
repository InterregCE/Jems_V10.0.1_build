package io.cloudflight.jems.server.project.service.application.start_second_step

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.application.workflow.states.ApprovedApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.model.ProjectVersionSummary
import io.cloudflight.jems.server.project.service.save_project_version.CreateNewProjectVersionInteractor
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

class StartSecondStepInteractorTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private val summary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.STEP1_APPROVED
        )
        private val userEntity = UserEntity(
            id = 2L,
            email = "some@applicant",
            name ="",
            surname = "",
            userRole = UserRoleEntity(0, "role"),
            password = "",
            userStatus = UserStatus.ACTIVE
        )
        private val projectVersionSummary = ProjectVersionSummary(
            version = "1.0",
            projectId = PROJECT_ID,
            createdAt = ZonedDateTime.now(),
            user = userEntity,
        )
    }

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var applicationStateFactory: ApplicationStateFactory

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var projectVersionPersistence: ProjectVersionPersistence

    @RelaxedMockK
    lateinit var securityService: SecurityService

    @RelaxedMockK
    lateinit var createNewProjectVersion: CreateNewProjectVersionInteractor

    @InjectMockKs
    private lateinit var startSecondStep: StartSecondStep

    @MockK
    lateinit var approvedState: ApprovedApplicationState

    @Test
    fun startSecondStep() {
        every { createNewProjectVersion.create(PROJECT_ID) } returns projectVersionSummary
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns summary
        every { applicationStateFactory.getInstance(any()) } returns approvedState
        every { approvedState.startSecondStep() } returns ApplicationStatus.DRAFT
        val currentUser: CurrentUser = mockk()
        every { securityService.currentUser } returns currentUser
        every { currentUser.user.email } returns userEntity.email

        val slotAudit = mutableListOf<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) }.returnsMany(Unit)

        assertThat(startSecondStep.startSecondStep(PROJECT_ID)).isEqualTo(ApplicationStatus.DRAFT)

        verify(exactly = 1) { auditPublisher.publishEvent(slotAudit[0]) }
        verify(exactly = 1) { auditPublisher.publishEvent(slotAudit[1]) }

        assertThat(slotAudit[0].auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_STATUS_CHANGED,
                project = AuditProject(id = PROJECT_ID.toString(), customIdentifier = "01", name = "project acronym"),
                description = "Project application status changed from STEP1_APPROVED to DRAFT"
            )
        )
        assertThat(slotAudit[1].auditCandidate).matches {
            it.action == AuditAction.APPLICATION_VERSION_RECORDED
                && it.project == AuditProject(id = PROJECT_ID.toString(), customIdentifier = "01", name = "project acronym")
                && it.description.startsWith("New project version \"V.1.0\" is recorded by user: some@applicant on") // ..timestamp could differ
        }
    }

}
