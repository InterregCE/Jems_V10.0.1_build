package io.cloudflight.jems.server.user.service.userrole.get_user_role

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

internal class GetUserRoleTest : UnitTest() {

    companion object {
        private const val ROLE_ID = 12L

        private val userRole = UserRole(
            id = ROLE_ID,
            name = "maintainer",
            permissions = setOf(UserRolePermission.ProjectSubmission)
        )

        private val userRoleSummary = UserRoleSummary(
            id = ROLE_ID,
            name = userRole.name,
        )
    }

    @MockK
    lateinit var persistence: UserRolePersistence

    @InjectMockKs
    lateinit var getUserRole: GetUserRole

    @Test
    fun getUserRoles() {
        every { persistence.findAll(any()) } returns PageImpl(listOf(userRoleSummary))
        assertThat(getUserRole.getUserRoles(Pageable.unpaged()).content).containsExactly(userRoleSummary)
    }

    @Test
    fun getUserRoleById() {
        every { persistence.getById(ROLE_ID) } returns userRole
        assertThat(getUserRole.getUserRoleById(ROLE_ID)).isEqualTo(userRole)
    }

}
