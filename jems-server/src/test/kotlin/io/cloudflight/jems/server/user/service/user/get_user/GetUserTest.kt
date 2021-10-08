package io.cloudflight.jems.server.user.service.user.get_user

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.user.service.model.UserWithPassword
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

internal class GetUserTest : UnitTest() {

    companion object {
        private const val USER_ID = 40L

        private val userRole = UserRole(
            id = 9L,
            name = "maintainer",
            permissions = setOf(UserRolePermission.ProjectSubmission)
        )

        private val user = User(
            id = USER_ID,
            email = "maintainer@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            userRole = userRole,
            userStatus = UserStatus.ACTIVE
        )
        private val userSummary = UserSummary(
            id = USER_ID,
            email = user.email,
            name = user.name,
            surname = user.surname,
            userRole = UserRoleSummary(id = userRole.id, name = userRole.name),
            userStatus = UserStatus.ACTIVE
        )

        private val userWithPassword = UserWithPassword(
            id = USER_ID,
            email = user.email,
            name = user.name,
            surname = user.surname,
            userRole = user.userRole,
            encodedPassword = "hash_pass",
            userStatus = UserStatus.ACTIVE
        )
    }

    @MockK
    lateinit var persistence: UserPersistence

    @InjectMockKs
    lateinit var getUser: GetUser

    @Test
    fun getUsers() {
        every { persistence.findAll(any(), null) } returns PageImpl(listOf(userSummary))
        assertThat(getUser.getUsers(Pageable.unpaged(), null).content).containsExactly(userSummary)
    }

    @Test
    fun getUserById() {
        every { persistence.getById(USER_ID) } returns userWithPassword
        assertThat(getUser.getUserById(USER_ID)).isEqualTo(user)
    }

}
