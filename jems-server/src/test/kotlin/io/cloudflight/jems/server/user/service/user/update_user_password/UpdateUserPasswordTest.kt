package io.cloudflight.jems.server.user.service.user.update_user_password

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.model.Password
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserSettings
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserWithPassword
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder

internal class UpdateUserPasswordTest : UnitTest() {

    companion object {
        private const val USER_ID = 14L
        private const val ROLE_ID = 221L

        private val oldUser = UserWithPassword(
            id = USER_ID,
            email = "maintainer_old@interact.eu",
            userSettings = UserSettings(sendNotificationsToEmail = false),
            name = "Michael_old",
            surname = "Schumacher_old",
            userRole = UserRole(
                id = ROLE_ID,
                name = "maintainer_old",
                permissions = setOf(UserRolePermission.ProjectSubmission)
            ),
            encodedPassword = "old_hash_pass",
            userStatus = UserStatus.ACTIVE
        )

        private fun initiator(id: Long): User {
            val user = mockk<User>()
            every { user.id } returns id
            every { user.email } returns "initiator@user"
            return user
        }

    }

    @MockK
    private lateinit var persistence: UserPersistence
    @MockK
    private lateinit var securityService: SecurityService
    @MockK
    private lateinit var passwordEncoder: PasswordEncoder
    @MockK
    private lateinit var generalValidator: GeneralValidatorService
    @MockK
    private lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var service: UpdateUserPassword

    @BeforeEach
    fun setup() {
        clearMocks(persistence)
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
            AppInputValidationException(emptyMap())
    }

    @Test
    fun resetUserPassword() {
        val slotInput = slot<String>()
        val slotRegex = slot<String>()
        every { generalValidator.matches(capture(slotInput), capture(slotRegex), "password", "user.password.constraints.not.satisfied") } returns emptyMap()

        every { persistence.getById(USER_ID) } returns oldUser
        every { passwordEncoder.encode("new_pass") } returns "new_pass_hash"
        every { persistence.updatePassword(USER_ID, any()) } answers { }

        every { securityService.currentUser?.user } returns initiator(USER_ID)
        val auditSlot = slot<JemsAuditEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        service.resetUserPassword(USER_ID, "new_pass")

        assertThat(slotInput.captured).isEqualTo("new_pass")
        assertThat(slotRegex.captured).isEqualTo("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{10,}).+\$")

        verify(exactly = 1) { persistence.updatePassword(USER_ID, "new_pass_hash") }
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(AuditCandidate(
            action = AuditAction.PASSWORD_CHANGED,
            project = null,
            entityRelatedId = USER_ID,
            description = "Password of user 'Michael_old Schumacher_old' (maintainer_old@interact.eu) has been reset",
        ))
    }

    @Test
    fun updateMyPassword() {
        val slotInput = slot<String>()
        val slotRegex = slot<String>()
        every { generalValidator.matches(capture(slotInput), capture(slotRegex), "password", "user.password.constraints.not.satisfied") } returns emptyMap()

        every { persistence.getById(USER_ID) } returns oldUser
        every { persistence.updatePassword(USER_ID, any()) } answers { }

        val auditSlot = slot<JemsAuditEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        every { securityService.getUserIdOrThrow() } returns USER_ID
        every { passwordEncoder.matches("old_raw_pass", "old_hash_pass") } returns true
        every { passwordEncoder.encode("new_pass") } returns "new_pass_hash"
        service.updateMyPassword(Password(oldPassword = "old_raw_pass", password = "new_pass"))

        assertThat(slotInput.captured).isEqualTo("new_pass")
        assertThat(slotRegex.captured).isEqualTo("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{10,}).+\$")

        verify(exactly = 1) { persistence.updatePassword(USER_ID, "new_pass_hash") }
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(AuditCandidate(
            action = AuditAction.PASSWORD_CHANGED,
            project = null,
            entityRelatedId = USER_ID,
            description = "Password of user 'Michael_old Schumacher_old' (maintainer_old@interact.eu) has been changed by himself/herself",
        ))
    }

    @Test
    fun `updateMyPassword - wrong old password`() {
        val slotInput = slot<String>()
        val slotRegex = slot<String>()
        every { generalValidator.matches(capture(slotInput), capture(slotRegex), "password", "user.password.constraints.not.satisfied") } returns emptyMap()

        every { persistence.getById(USER_ID) } returns oldUser
        every { persistence.updatePassword(USER_ID, any()) } answers { }

        every { securityService.getUserIdOrThrow() } returns USER_ID
        every { passwordEncoder.matches("old_pass_wrong", "old_hash_pass") } returns false
        assertThrows<UserOldPasswordDoesNotMatch> {
            service.updateMyPassword(Password(oldPassword = "old_pass_wrong", password = "new_pass"))
        }

        verify(exactly = 0) { persistence.updatePassword(any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

}
