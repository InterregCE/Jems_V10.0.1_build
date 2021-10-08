package io.cloudflight.jems.server.user.service.user.update_user

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.common.event.JemsEvent
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.mail.confirmation.service.MailConfirmationService
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserChange
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserWithPassword
import io.cloudflight.jems.server.user.service.user.ConfirmUserEmailEvent
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

internal class UpdateUserTest : UnitTest() {

    companion object {
        private const val USER_ID = 6L
        private const val ROLE_ID = 9L

        private val oldUser = UserWithPassword(
            id = USER_ID,
            email = "maintainer_old@interact.eu",
            name = "Michael_old",
            surname = "Schumacher_old",
            userRole = UserRole(
                id = 296L,
                name = "maintainer_old",
                permissions = setOf(UserRolePermission.ProjectSubmission)
            ),
            encodedPassword = "hash_pass",
            userStatus = UserStatus.ACTIVE
        )

    }

    @MockK
    lateinit var persistence: UserPersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var mailConfirmationService: MailConfirmationService

    @InjectMockKs
    lateinit var updateUser: UpdateUser

    @Test
    fun updateUser() {
        val changeUser = UserChange(
            id = USER_ID,
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
        every { persistence.getById(USER_ID) } returns oldUser
        every { persistence.emailExists("maintainer@interact.eu") } returns false
        every { persistence.userRoleExists(ROLE_ID) } returns true
        every { persistence.update(changeUser) } returns expectedUser

        assertThat(updateUser.updateUser(changeUser)).isEqualTo(expectedUser)
    }

    @Test
    fun `updateUser - user email already taken`() {
        val changeUser = UserChange(
            id = USER_ID,
            email = "maintainer@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            userRoleId = ROLE_ID,
            userStatus = UserStatus.ACTIVE
        )

        every { persistence.getById(USER_ID) } returns oldUser//.copy(email = "maintainer@interact.eu")
        every { persistence.emailExists("maintainer@interact.eu") } returns true

        assertThrows<UserEmailAlreadyTaken> { updateUser.updateUser(changeUser) }
    }

    @Test
    fun `updateUser - user role does not exist`() {
        val changeUser = UserChange(
            id = USER_ID,
            email = "maintainer@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            userRoleId = ROLE_ID,
            userStatus = UserStatus.ACTIVE
        )

        every { persistence.getById(USER_ID) } returns oldUser.copy(email = "maintainer@interact.eu")
        every { persistence.emailExists("maintainer@interact.eu") } returns true
        every { persistence.userRoleExists(ROLE_ID) } returns false

        assertThrows<UserRoleNotFound> { updateUser.updateUser(changeUser) }
    }

}
