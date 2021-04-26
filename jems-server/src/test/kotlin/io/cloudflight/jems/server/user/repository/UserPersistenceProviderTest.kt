package io.cloudflight.jems.server.user.repository

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.entity.UserRolePermissionEntity
import io.cloudflight.jems.server.user.entity.UserRolePermissionId
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.repository.user.UserRoleNotFound
import io.cloudflight.jems.server.user.repository.userrole.UserRolePermissionRepository
import io.cloudflight.jems.server.user.repository.userrole.UserRoleRepository
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserChange
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectSubmission
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional

internal class UserPersistenceProviderTest : UnitTest() {

    companion object {
        private const val USER_ID = 1L;
        private const val ROLE_ID = 2L

        private val userRoleEntity = UserRoleEntity(
            id = ROLE_ID,
            name = "ruler"
        )
        private val userEntity = UserEntity(
            id = USER_ID,
            email = "replace",
            name = "replace",
            surname = "replace",
            password = "test",
            userRole = userRoleEntity
        )
        private val permissionEntity = UserRolePermissionEntity(
            UserRolePermissionId(
                userRole = userRoleEntity,
                permission = ProjectSubmission,
            )
        )
    }

    @MockK
    lateinit var userRepo: UserRepository
    @MockK
    lateinit var userRoleRepo: UserRoleRepository
    @MockK
    lateinit var userRolePermissionRepo: UserRolePermissionRepository

    @InjectMockKs
    private lateinit var persistence: UserPersistenceProvider

    @Test
    fun `update user - success`() {
        val change = UserChange(USER_ID, "email", "name", "surname", ROLE_ID)
        every { userRepo.findById(USER_ID) } returns Optional.of(userEntity)
        every { userRoleRepo.findById(ROLE_ID) } returns Optional.of(userRoleEntity)
        every { userRolePermissionRepo.findAllByIdUserRoleId(ROLE_ID) } returns listOf(permissionEntity)

        assertThat(persistence.update(change)).isEqualTo(
            User(
                id = USER_ID,
                name = change.name,
                email = change.email,
                surname = change.surname,
                userRole = UserRole(change.userRoleId, userRoleEntity.name, setOf(ProjectSubmission))
            )
        )
    }

    @Test
    fun `update user - role not found`() {
        val change = UserChange(USER_ID, "email", "name", "surname", -1)
        every { userRepo.findById(USER_ID) } returns Optional.of(userEntity)
        every { userRoleRepo.findById(-1) } returns Optional.empty()

        assertThrows<UserRoleNotFound> { persistence.update(change) }
    }

}
