package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.ProjectVersionEntity
import io.cloudflight.jems.server.project.entity.ProjectVersionId
import io.cloudflight.jems.server.project.entity.ProjectVersionRow
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.time.ZonedDateTime

class ProjectVersionPersistenceProviderTest : UnitTest() {

    private val projectId = 11L
    private val version = ProjectVersionUtils.DEFAULT_VERSION
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
    private val projectVersionEntity = ProjectVersionEntity(
        id = ProjectVersionId("1.1", projectId),
        createdAt = ZonedDateTime.now(),
        user = user
    )

    @MockK
    lateinit var projectVersionRepository: ProjectVersionRepository

    @MockK
    lateinit var userRepository: UserRepository

    @InjectMockKs
    lateinit var projectVersionPersistenceProvider: ProjectVersionPersistenceProvider

    @Test
    fun `should create a new version for the project when there is no problem`() {
        val entitySlot = slot<ProjectVersionEntity>()
        every { projectVersionRepository.save(capture(entitySlot)) } answers { entitySlot.captured }
        every { projectVersionRepository.endCurrentVersion(projectId) } returns  Unit
        every { userRepository.getById(userId) } returns user
        val projectVersion =
            projectVersionPersistenceProvider.createNewVersion(projectId, ApplicationStatus.SUBMITTED, version, userId)
        assertThat(projectVersion).isEqualTo(entitySlot.captured.toProjectVersion(ApplicationStatus.SUBMITTED, true))
    }

    @Test
    fun `should return latest version of project when there is no problem`() {
        every { projectVersionRepository.findLatestVersion(projectId) } returns projectVersionEntity.id.version
        val latestProjectVersion = projectVersionPersistenceProvider.getLatestVersionOrNull(projectId)
        assertThat(latestProjectVersion).isEqualTo(projectVersionEntity.id.version)
    }

    @Test
    fun `should return the list of all versions`() {
        val entities = listOf(projectVersionEntity)
        val versionMock: ProjectVersionRow = mockk()
        every { versionMock.version } returns projectVersionEntity.id.version
        every { versionMock.projectId } returns projectVersionEntity.id.projectId
        every { versionMock.rowEnd } returns projectVersionEntity.rowEnd
        every { versionMock.createdAt } returns Timestamp.from(projectVersionEntity.createdAt.toInstant())
        every { versionMock.status } returns ApplicationStatus.DRAFT
        every { versionMock.userId } returns projectVersionEntity.user.id
        every { versionMock.email } returns projectVersionEntity.user.email
        every { versionMock.name } returns projectVersionEntity.user.name
        every { versionMock.surname } returns projectVersionEntity.user.surname
        every { versionMock.userStatus } returns projectVersionEntity.user.userStatus
        every { versionMock.roleId } returns projectVersionEntity.user.userRole.id
        every { versionMock.roleName } returns projectVersionEntity.user.userRole.name
        every { projectVersionRepository.findAllVersionsByProjectId(projectId) } returns listOf(versionMock)

        val versions = projectVersionPersistenceProvider.getAllVersionsByProjectId(projectId)


        assertThat(listOf(versionMock).toProjectVersion())
            .usingRecursiveFieldByFieldElementComparator()
            .isEqualTo(versions)
    }
}
