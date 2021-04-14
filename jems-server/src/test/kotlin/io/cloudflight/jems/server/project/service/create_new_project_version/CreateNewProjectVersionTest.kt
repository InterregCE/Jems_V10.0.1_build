package io.cloudflight.jems.server.project.service.create_new_project_version

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.model.ProjectVersion
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.user.entity.UserRole
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.sql.Timestamp
import java.time.LocalDateTime

internal class CreateNewProjectVersionTest : UnitTest() {

    private val projectId = 11L
    private val userId = 1L
    private val user = User(
        id = userId,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = UserRole(id = 1, name = "ADMIN"),
        password = "hash_pass"
    )
    private val currentProjectVersion = ProjectVersion(
        1,
        projectId,
        createdAt = Timestamp.valueOf(LocalDateTime.now()),
        user,
        ApplicationStatus.RETURNED_TO_APPLICANT
    )
    private val newProjectVersion = ProjectVersion(
        2,
        projectId,
        createdAt = Timestamp.valueOf(LocalDateTime.now()),
        user,
        ApplicationStatus.SUBMITTED
    )

    private val projectSummary = ProjectSummary(
        id = projectId,
        acronym = "Gleason Inc",
        status = ApplicationStatus.SUBMITTED
    )

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var projectVersionPersistence: ProjectVersionPersistence

    @MockK
    lateinit var securityService: SecurityService

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var createNewProjectVersion: CreateNewProjectVersion

    @Test
    fun `should create a new version for the project when there is no problem`() {
        every { securityService.getUserIdOrThrow() } returns userId
        every { projectVersionPersistence.getLatestVersionOrNull(projectId) } returns currentProjectVersion
        every {
            projectVersionPersistence.createNewVersion(
                projectId, currentProjectVersion.version + 1, ApplicationStatus.SUBMITTED, userId
            )
        } returns newProjectVersion
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary
        val auditEventSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditEventSlot)) } returns Unit

        val createdProjectVersion = createNewProjectVersion.create(projectId, ApplicationStatus.SUBMITTED)

        verify(exactly = 1) { auditPublisher.publishEvent(auditEventSlot.captured) }

        assertThat(createdProjectVersion).isEqualTo(newProjectVersion)
    }
}
