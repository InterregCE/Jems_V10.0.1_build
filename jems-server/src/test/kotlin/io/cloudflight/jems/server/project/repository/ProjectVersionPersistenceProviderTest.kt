package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.ProjectVersionEntity
import io.cloudflight.jems.server.project.entity.ProjectVersionId
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.time.LocalDateTime

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
        password = "hash_pass"
    )
    private val projectVersionEntity = ProjectVersionEntity(
        id = ProjectVersionId("1.1", projectId),
        createdAt = Timestamp.valueOf(LocalDateTime.now()),
        status = ApplicationStatus.SUBMITTED,
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
        every { userRepository.getOne(userId) } returns user
        val projectVersion =
            projectVersionPersistenceProvider.createNewVersion(projectId, version, ApplicationStatus.SUBMITTED, userId)
        assertThat(projectVersion).isEqualTo(entitySlot.captured.toProjectVersion())
    }

    @Test
    fun `should return latest version of project when there is no problem`() {
        every { projectVersionRepository.findFirstByIdProjectIdOrderByCreatedAtDesc(projectId) } returns projectVersionEntity
        val latestProjectVersion = projectVersionPersistenceProvider.getLatestVersionOrNull(projectId)
        assertThat(latestProjectVersion).isEqualTo(projectVersionEntity.toProjectVersion())
    }
}
