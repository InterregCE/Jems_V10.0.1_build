package io.cloudflight.jems.server.user.service.user.create_user

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.config.AppSecurityProperties
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.confirmation.UserConfirmationPersistence
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserChange
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder

internal class CreateUserTest : UnitTest() {

    companion object {
        private const val USER_ID = 11L
        private const val ROLE_ID = 8L
    }

    @MockK
    lateinit var persistence: UserPersistence

    @MockK
    lateinit var appSecurityProperties: AppSecurityProperties

    @MockK
    lateinit var passwordEncoder: PasswordEncoder

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var userConfirmationPersistence: UserConfirmationPersistence

    @InjectMockKs
    lateinit var createUser: CreateUser

    @BeforeAll
    fun setup() {
        every { passwordEncoder.encode(any()) } answers { "hash_${firstArg<String>()}" }
        every { appSecurityProperties.defaultPasswordPrefix } returns "pass_prefix_"
    }

    @Test
    fun createUser() {
        val createUserModel = UserChange(
            id = 0L,
            email = "maintainer@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            userRoleId = ROLE_ID,
            userStatus = UserStatus.UNCONFIRMED
        )
        val expectedUser = User(
            id = USER_ID,
            email = "maintainer@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            userRole = UserRole(
                id = ROLE_ID,
                name = "maintainer",
                permissions = emptySet()
            ),
            userStatus = UserStatus.UNCONFIRMED
        )

        every { persistence.userRoleExists(ROLE_ID) } returns true
        every { persistence.emailExists("maintainer@interact.eu") } returns false
        val slotPassword = slot<String>()
        every { persistence.create(createUserModel, capture(slotPassword)) } returns expectedUser

        assertThat(createUser.createUser(createUserModel)).isEqualTo(expectedUser)
        assertThat(slotPassword.captured).isEqualTo("hash_pass_prefix_maintainer@interact.eu")
    }

    @Test
    fun `createUser - id filled in`() {
        val createUserModel = UserChange(
            id = 844L,
            email = "maintainer@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            userRoleId = ROLE_ID,
            userStatus = UserStatus.ACTIVE
        )

        assertThrows<UserIdCannotBeSpecified> { createUser.createUser(createUserModel) }
    }

    @Test
    fun `createUser - user role not exist`() {
        val createUserModel = UserChange(
            id = 0L,
            email = "maintainer@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            userRoleId = -45L,
            userStatus = UserStatus.ACTIVE
        )

        every { persistence.userRoleExists(-45L) } returns false

        assertThrows<UserRoleNotFound> { createUser.createUser(createUserModel) }
    }

    @Test
    fun `createUser - user email already taken`() {
        val createUserModel = UserChange(
            id = 0L,
            email = "already@in.use",
            name = "Michael",
            surname = "Schumacher",
            userRoleId = ROLE_ID,
            userStatus = UserStatus.ACTIVE
        )

        every { persistence.userRoleExists(ROLE_ID) } returns true
        every { persistence.emailExists("already@in.use") } returns true

        assertThrows<UserEmailAlreadyTaken> { createUser.createUser(createUserModel) }
    }

}
