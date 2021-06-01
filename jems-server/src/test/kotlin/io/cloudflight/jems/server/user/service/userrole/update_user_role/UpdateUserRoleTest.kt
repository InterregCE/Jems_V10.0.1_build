package io.cloudflight.jems.server.user.service.userrole.update_user_role

import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Optional

@ExtendWith(MockKExtension::class)
internal class UpdateUserRoleTest {

    companion object {
        private const val ROLE_ID = 2L

        private val userRoleUpdate = UserRole(
            id = ROLE_ID,
            name = "maintainer",
            permissions = setOf(UserRolePermission.ProjectSubmission)
        )
    }

    @MockK
    lateinit var persistence: UserRolePersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var updateUserRole: UpdateUserRole

    @Test
    fun `updateUserRole - change to different role - OK`() {
        every { persistence.existsById(ROLE_ID) } returns true
        every { persistence.findUserRoleByName(userRoleUpdate.name) } returns Optional.empty()
        every { persistence.update(any()) } returnsArgument 0

        assertThat(updateUserRole.updateUserRole(userRoleUpdate)).isEqualTo(userRoleUpdate)
        verify(exactly = 1) { persistence.update(userRoleUpdate) }
    }

    @Test
    fun `updateUserRole - no change in role - OK`() {
        every { persistence.existsById(ROLE_ID) } returns true
        every { persistence.findUserRoleByName(userRoleUpdate.name) } returns Optional.of(UserRoleSummary(id = ROLE_ID, name = userRoleUpdate.name))
        every { persistence.update(any()) } returnsArgument 0

        assertThat(updateUserRole.updateUserRole(userRoleUpdate)).isEqualTo(userRoleUpdate)
        verify(exactly = 1) { persistence.update(userRoleUpdate) }
    }

    @Test
    fun `updateUserRole - role does not exist`() {
        every { persistence.existsById(ROLE_ID) } returns false
        assertThrows<UserRoleNotFound> { updateUserRole.updateUserRole(userRoleUpdate) }
    }

    @Test
    fun `updateUserRole - role name already taken`() {
        every { persistence.existsById(ROLE_ID) } returns true
        every { persistence.findUserRoleByName(userRoleUpdate.name) } returns Optional.of(UserRoleSummary(id = 126L, name = userRoleUpdate.name))
        assertThrows<UserRoleNameAlreadyTaken> { updateUserRole.updateUserRole(userRoleUpdate) }
    }

}
