package io.cloudflight.jems.server.project.service.save_project_version

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.model.ProjectVersion
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

internal class SaveProjectVersionTest : UnitTest() {

    private val projectId = 11L
    private val userId = 1L
    private val user = UserEntity(
        id = userId,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = UserRoleEntity(id = 1, name = "ADMIN"),
        password = "hash_pass",
        userStatus = UserStatus.ACTIVE
    )
    private val currentProjectVersion = ProjectVersion(
        "1.0",
        projectId,
        createdAt = ZonedDateTime.now(),
        user,
        ApplicationStatus.RETURNED_TO_APPLICANT
    )
    private val newProjectVersion = ProjectVersion(
        "2.0",
        projectId,
        createdAt = ZonedDateTime.now(),
        user,
        ApplicationStatus.SUBMITTED
    )

    private val projectSummary = ProjectSummary(
        id = projectId,
        customIdentifier = "01",
        callName = "",
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
    lateinit var saveProjectVersion: SaveProjectVersion

    @Test
    fun `should create a new version for the project when there is no problem`() {
        every { securityService.getUserIdOrThrow() } returns userId
        every { projectVersionPersistence.getLatestVersionOrNull(projectId) } returns currentProjectVersion
        every {
            projectVersionPersistence.createNewVersion(
                projectId,
                ProjectVersionUtils.increaseMajor(currentProjectVersion.version),
                ApplicationStatus.SUBMITTED,
                userId
            )
        } returns newProjectVersion
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary
        val auditEventSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditEventSlot)) } returns Unit

        val createdProjectVersion = saveProjectVersion.createNewVersion(projectId, ApplicationStatus.SUBMITTED)

        verify(exactly = 1) { auditPublisher.publishEvent(auditEventSlot.captured) }

        assertThat(createdProjectVersion).isEqualTo(newProjectVersion)
    }
}
