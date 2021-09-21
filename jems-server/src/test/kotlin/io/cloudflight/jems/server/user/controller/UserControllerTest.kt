package io.cloudflight.jems.server.user.controller

import io.cloudflight.jems.api.user.dto.PasswordDTO
import io.cloudflight.jems.api.user.dto.UserChangeDTO
import io.cloudflight.jems.api.user.dto.UserDTO
import io.cloudflight.jems.api.user.dto.UserRoleDTO
import io.cloudflight.jems.api.user.dto.UserRolePermissionDTO
import io.cloudflight.jems.api.user.dto.UserRoleSummaryDTO
import io.cloudflight.jems.api.user.dto.UserSearchRequestDTO
import io.cloudflight.jems.api.user.dto.UserSummaryDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.user.service.model.Password
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserChange
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserSearchRequest
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.user.service.user.create_user.CreateUserInteractor
import io.cloudflight.jems.server.user.service.user.get_user.GetUserInteractor
import io.cloudflight.jems.server.user.service.user.update_user.UpdateUserInteractor
import io.cloudflight.jems.server.user.service.user.update_user_password.UpdateUserPasswordInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class UserControllerTest : UnitTest() {

    companion object {
        private const val USER_ID = 7L
        private const val ROLE_ID = 4L

        private val userSearchRequestDTO = UserSearchRequestDTO(
            email = "email@smaple.com",
            name = "name",
            surname = "surname",
            roles = setOf(ROLE_ID)
        )

        private val userSearchRequest = UserSearchRequest(
            email = "email@smaple.com",
            name = "name",
            surname = "surname",
            roles = setOf(ROLE_ID)
        )

        private val userRoleSummary = UserRoleSummary(
            id = ROLE_ID,
            name = "maintainer",
        )
        private val userRole = UserRole(
            id = ROLE_ID,
            name = userRoleSummary.name,
            permissions = setOf(UserRolePermission.ProjectSubmission)
        )
        private val userSummary = UserSummary(
            id = USER_ID,
            email = "maintainer@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            userRole = userRoleSummary
        )
        private val user = User(
            id = USER_ID,
            email = userSummary.email,
            name = userSummary.name,
            surname = userSummary.surname,
            userRole = userRole
        )

        private val expectedUserRoleSummary = UserRoleSummaryDTO(
            id = ROLE_ID,
            name = "maintainer",
            defaultForRegisteredUser = false
        )
        private val expectedUserRole = UserRoleDTO(
            id = ROLE_ID,
            name = expectedUserRoleSummary.name,
            defaultForRegisteredUser = false,
            permissions = listOf(UserRolePermissionDTO.ProjectSubmission)
        )
        private val expectedUserSummary = UserSummaryDTO(
            id = USER_ID,
            email = "maintainer@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            userRole = expectedUserRoleSummary
        )
        private val expectedUser = UserDTO(
            id = USER_ID,
            email = expectedUserSummary.email,
            name = expectedUserSummary.name,
            surname = expectedUserSummary.surname,
            userRole = expectedUserRole
        )
    }

    @MockK
    lateinit var getUserInteractor: GetUserInteractor

    @MockK
    lateinit var createUserInteractor: CreateUserInteractor

    @MockK
    lateinit var updateUserInteractor: UpdateUserInteractor

    @MockK
    lateinit var updateUserPasswordInteractor: UpdateUserPasswordInteractor

    @InjectMockKs
    private lateinit var controller: UserController

    @Test
    fun list() {
        val slot = slot<UserSearchRequest>()
        every { getUserInteractor.getUsers(any(), capture(slot)) } returns PageImpl(listOf(userSummary))
        assertThat(controller.list(Pageable.unpaged(), userSearchRequestDTO).content).containsExactly(expectedUserSummary)
        assertThat(slot.captured).isEqualTo(userSearchRequest)
    }

    @Test
    fun createUser() {
        val userCreate = UserChangeDTO(
            email = userSummary.email,
            name = userSummary.name,
            surname = userSummary.surname,
            userRoleId = ROLE_ID,
        )

        val slotUserChange = slot<UserChange>()
        every { createUserInteractor.createUser(capture(slotUserChange)) } returns user
        assertThat(controller.createUser(userCreate)).isEqualTo(expectedUser)
        assertThat(slotUserChange.captured).isEqualTo(
            UserChange(
                id = 0L,
                email = "maintainer@interact.eu",
                name = "Michael",
                surname = "Schumacher",
                userRoleId = ROLE_ID,
            )
        )
    }

    @Test
    fun updateUser() {
        val userUpdate = UserChangeDTO(
            id = USER_ID,
            email = userSummary.email,
            name = userSummary.name,
            surname = userSummary.surname,
            userRoleId = ROLE_ID,
        )

        val slotUserChange = slot<UserChange>()
        every { updateUserInteractor.updateUser(capture(slotUserChange)) } returns user
        assertThat(controller.updateUser(userUpdate)).isEqualTo(expectedUser)
        assertThat(slotUserChange.captured).isEqualTo(
            UserChange(
                id = USER_ID,
                email = "maintainer@interact.eu",
                name = "Michael",
                surname = "Schumacher",
                userRoleId = ROLE_ID,
            )
        )
    }

    @Test
    fun getById() {
        every { getUserInteractor.getUserById(USER_ID) } returns user
        assertThat(controller.getById(USER_ID)).isEqualTo(expectedUser)
    }

    @Test
    fun resetPassword() {
        every { updateUserPasswordInteractor.resetUserPassword(USER_ID, "plain_pass") } answers {}
        assertDoesNotThrow { controller.resetPassword(USER_ID, "plain_pass") }
        verify(exactly = 1) { updateUserPasswordInteractor.resetUserPassword(USER_ID, "plain_pass") }
    }

    @Test
    fun changeMyPassword() {
        val newPass = "new_plain_pass"
        val oldPass = "old_plain_pass"
        val passwordData = Password(password = newPass, oldPassword = oldPass)

        every { updateUserPasswordInteractor.updateMyPassword(passwordData) } answers {}
        assertDoesNotThrow { controller.changeMyPassword(PasswordDTO(password = newPass, oldPassword = oldPass)) }
        verify(exactly = 1) { updateUserPasswordInteractor.updateMyPassword(passwordData) }
    }
}
