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
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.time.ZoneOffset
import java.time.ZonedDateTime

class ProjectVersionPersistenceProviderTest : UnitTest() {

    private val projectId = 11L
    private val version = ProjectVersionUtils.DEFAULT_VERSION
    private val userId = 1L
    private val user = UserEntity(
        id = userId,
        email = "admin@admin.dev",
        sendNotificationsToEmail = false,
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

    @MockK
    lateinit var optimizationProjectVersionRepository: OptimizationProjectVersionRepository

    @InjectMockKs
    lateinit var projectVersionPersistenceProvider: ProjectVersionPersistenceProvider

    @Test
    fun `should create a new version for the project when there is no problem`() {
        val entitySlot = slot<ProjectVersionEntity>()
        every { projectVersionRepository.save(capture(entitySlot)) } answers { entitySlot.captured }
        every { projectVersionRepository.endCurrentVersion(projectId) } returns  Unit
        every { userRepository.getById(userId) } returns user
        val projectVersionSummary =
            projectVersionPersistenceProvider.createNewVersion(projectId, version, userId)
        assertThat(projectVersionSummary).isEqualTo(entitySlot.captured.toProjectVersionSummary())
    }

    @Test
    fun `should return latest version of project when there is no problem`() {
        every { projectVersionRepository.findLatestVersion(projectId) } returns projectVersionEntity.id.version
        val latestProjectVersion = projectVersionPersistenceProvider.getLatestVersionOrNull(projectId)
        assertThat(latestProjectVersion).isEqualTo(projectVersionEntity.id.version)
    }

    @Test
    fun `should return the list of all versions`() {
        val versionMock: ProjectVersionRow = mockk()
        every { versionMock.version } returns projectVersionEntity.id.version
        every { versionMock.projectId } returns projectVersionEntity.id.projectId
        every { versionMock.rowEnd } returns projectVersionEntity.rowEnd
        every { versionMock.createdAt } returns Timestamp.from(projectVersionEntity.createdAt.toInstant())
        every { versionMock.status } returns ApplicationStatus.DRAFT
        every { versionMock.roleId } returns projectVersionEntity.user.userRole.id
        every { versionMock.roleName } returns projectVersionEntity.user.userRole.name
        every { projectVersionRepository.findAllVersionsByProjectId(projectId) } returns listOf(versionMock)

        val versions = projectVersionPersistenceProvider.getAllVersionsByProjectId(projectId)


        assertThat(listOf(versionMock).toProjectVersion())
            .usingRecursiveFieldByFieldElementComparator()
            .isEqualTo(versions)
    }

    @Test
    fun `should return all versions of all projects`() {
        val project1row = object : ProjectVersionRow {
            override val version = "1.0"
            override val projectId = 1L
            override val rowEnd = Timestamp(10)
            override val createdAt = Timestamp(20)
            override val status = ApplicationStatus.DRAFT
            override val roleId =3L
            override var roleName = "roleName"
        }

        val project2row = object : ProjectVersionRow {
            override val version = "2.0"
            override val projectId = 2L
            override val rowEnd = null
            override val createdAt = Timestamp(30)
            override val status = ApplicationStatus.SUBMITTED
            override val roleId =3L
            override var roleName = "roleName2"
        }
        every { projectVersionRepository.findAllVersions() } returns listOf(project1row, project1row, project2row, project2row)
        val result = projectVersionPersistenceProvider.getAllVersions()
        assertThat(result.size).isEqualTo(2)
        assertThat(result[0].version).isEqualTo("1.0")
        assertThat(result[0].projectId).isEqualTo(1L)
        assertThat(result[0].createdAt).isEqualTo(ZonedDateTime.of(Timestamp(20).toLocalDateTime(), ZoneOffset.UTC))
        assertThat(result[0].status).isEqualTo(ApplicationStatus.DRAFT)
        assertThat(result[0].current).isEqualTo(false)

        assertThat(result[1].version).isEqualTo("2.0")
        assertThat(result[1].projectId).isEqualTo(2L)
        assertThat(result[1].createdAt).isEqualTo(ZonedDateTime.of(Timestamp(30).toLocalDateTime(), ZoneOffset.UTC))
        assertThat(result[1].status).isEqualTo(ApplicationStatus.SUBMITTED)
        assertThat(result[1].current).isEqualTo(true)
    }

    @Test
    fun `should add new row with timestamp for the approved application`() {
        every { optimizationProjectVersionRepository.saveOptimizationProjectVersion(projectId) } just Runs

        projectVersionPersistenceProvider.saveTimestampForApprovedApplication(projectId)
        verify(atLeast = 1) { optimizationProjectVersionRepository.saveOptimizationProjectVersion(projectId) }
    }

    @Test
    fun `should update timestamp for the approved modification`() {
        every { optimizationProjectVersionRepository.updateOptimizationProjectVersion(projectId) } just Runs

        projectVersionPersistenceProvider.updateTimestampForApprovedModification(projectId)
        verify(atLeast = 1) { optimizationProjectVersionRepository.updateOptimizationProjectVersion(projectId) }
    }
}
