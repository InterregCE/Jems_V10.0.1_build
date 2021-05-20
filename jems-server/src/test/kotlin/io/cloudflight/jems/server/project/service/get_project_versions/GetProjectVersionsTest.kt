package io.cloudflight.jems.server.project.service.get_project_versions

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectVersion
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

internal class GetProjectVersionsTest : UnitTest() {

    private val result = listOf(
        ProjectVersion(
            version = "1.0",
            projectId = 1,
            createdAt = ZonedDateTime.now(),
            user = UserEntity(
                id = 1,
                name = "Name",
                password = "hash",
                email = "admin@admin.dev",
                surname = "surname",
                userRole = UserRoleEntity(id = 1, name = "ADMIN")
            ),
            status = ApplicationStatus.APPROVED,
        )
    )

    @MockK
    lateinit var persistence: ProjectVersionPersistence

    @InjectMockKs
    lateinit var getVersions: GetProjectVersions

    @Test
    fun getProjectVersions() {
        every { persistence.getAllVersionsByProjectId(1L) } returns result
        assertThat(getVersions.getProjectVersions(1L)).isEqualTo(result)
    }
}
