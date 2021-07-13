package io.cloudflight.jems.server.user.controller

import io.cloudflight.jems.api.user.dto.UserRoleCreateDTO
import io.cloudflight.jems.api.user.dto.UserRoleDTO
import io.cloudflight.jems.api.user.dto.UserRolePermissionDTO
import io.cloudflight.jems.api.user.dto.UserRoleSummaryDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.userrole.get_role.GetDefaultUserRoleInteractor
import io.cloudflight.jems.server.programme.service.userrole.update_role.UpdateDefaultUserRoleFailed
import io.cloudflight.jems.server.programme.service.userrole.update_role.UpdateDefaultUserRoleInteractor
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRoleCreate
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.userrole.create_user_role.CreateUserRoleInteractor
import io.cloudflight.jems.server.user.service.userrole.get_user_role.GetUserRoleInteractor
import io.cloudflight.jems.server.user.service.userrole.update_user_role.UpdateUserRoleInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class UserRoleControllerTest : UnitTest() {

    companion object {
        private const val ROLE_ID = 9L
        private val userRoleSummary = UserRoleSummary(
            id = ROLE_ID,
            name = "maintainer",
        )
        private val userRole = UserRole(
            id = ROLE_ID,
            name = userRoleSummary.name,
            permissions = setOf(UserRolePermission.ProjectSubmission)
        )

        private val expectedUserRoleSummary = UserRoleSummaryDTO(
            id = ROLE_ID,
            name = "maintainer",
        )
        private val expectedUserRole = UserRoleDTO(
            id = ROLE_ID,
            name = expectedUserRoleSummary.name,
            permissions = listOf(UserRolePermissionDTO.ProjectSubmission)
        )
    }

    @MockK
    lateinit var getUserRoleInteractor: GetUserRoleInteractor
    @MockK
    lateinit var createUserRoleInteractor: CreateUserRoleInteractor
    @MockK
    lateinit var updateUserRoleInteractor: UpdateUserRoleInteractor

    @MockK
    lateinit var getDefaultUserRoleInteractor: GetDefaultUserRoleInteractor
    @MockK
    lateinit var updateDefaultUserRoleInteractor: UpdateDefaultUserRoleInteractor

    @InjectMockKs
    private lateinit var controller: UserRoleController

    @Test
    fun list() {
        every { getUserRoleInteractor.getUserRoles(any()) } returns PageImpl(listOf(userRoleSummary))
        assertThat(controller.list(Pageable.unpaged()).content).containsExactly(expectedUserRoleSummary)
    }

    @Test
    fun createUserRole() {
        val userRoleCreate = UserRoleCreateDTO(
            name = "maintainer",
            permissions = setOf(UserRolePermissionDTO.ProjectSubmission),
        )

        val slotUserRoleCreate = slot<UserRoleCreate>()
        every { createUserRoleInteractor.createUserRole(capture(slotUserRoleCreate)) } returns userRole
        assertThat(controller.createUserRole(userRoleCreate)).isEqualTo(expectedUserRole)
        assertThat(slotUserRoleCreate.captured).isEqualTo(
            UserRoleCreate(
                name = "maintainer",
                permissions = setOf(UserRolePermission.ProjectSubmission),
            )
        )
    }

    @Test
    fun updateUserRole() {
        val userRoleUpdate = UserRoleDTO(
            id = ROLE_ID,
            name = "maintainer",
            permissions = listOf(UserRolePermissionDTO.ProjectSubmission),
        )

        every { updateUserRoleInteractor.updateUserRole(any()) } returnsArgument 0
        assertThat(controller.updateUserRole(userRoleUpdate)).isEqualTo(expectedUserRole)
    }

    @Test
    fun getById() {
        every { getUserRoleInteractor.getUserRoleById(ROLE_ID) } returns userRole
        assertThat(controller.getById(ROLE_ID)).isEqualTo(expectedUserRole)
    }

    @Test
    fun getDefaultUserRole() {
        every { getDefaultUserRoleInteractor.getDefault() } returns 1L
        assertThat(controller.getDefault()).isEqualTo(1L)
    }

    @Test
    fun setDefaultUserRole() {
        every { updateDefaultUserRoleInteractor.update(1L) } returns Unit
        controller.setDefault(1L)
        verify { updateDefaultUserRoleInteractor.update(1L) }
    }

    @Test
    fun setDefaultUserRoleException() {
        every { updateDefaultUserRoleInteractor.update(1L) } throws UpdateDefaultUserRoleFailed(Exception())
        assertThrows<UpdateDefaultUserRoleFailed> { controller.setDefault(1L) }
    }
}
