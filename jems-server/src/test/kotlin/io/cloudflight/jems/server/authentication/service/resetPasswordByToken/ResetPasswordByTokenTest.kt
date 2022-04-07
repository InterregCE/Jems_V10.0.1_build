package io.cloudflight.jems.server.authentication.service.resetPasswordByToken

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.PasswordResetToken
import io.cloudflight.jems.server.authentication.service.SecurityPersistence
import io.cloudflight.jems.server.authentication.service.emailPasswordResetLink.RESET_PASSWORD_TOKEN_VALIDITY_PERIOD_IN_HOUR
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.common.validator.PASSWORD_REGEX
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserWithPassword
import io.cloudflight.jems.server.user.service.user.PASSWORD_ERROR_KEY
import io.cloudflight.jems.server.user.service.user.PASSWORD_FIELD_NAME
import io.cloudflight.jems.server.utils.partner.userSummary
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant
import java.util.UUID


internal class ResetPasswordByTokenTest : UnitTest() {

    private val validToken = UUID.randomUUID()
    private val validPassword = "!QAZ2wsx3edc"
    private val user = userSummary(1L, 2L)
    private val userWithPassword = UserWithPassword(
        id = user.id,
        email = user.email,
        name = user.name,
        surname = user.surname,
        userRole = UserRole(user.userRole.id, user.userRole.name, emptySet(), user.userRole.isDefault),
        encodedPassword = "hash_pass",
        userStatus = UserStatus.ACTIVE
    )

    @MockK
    lateinit var userPersistence: UserPersistence

    @MockK
    lateinit var securityPersistence: SecurityPersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @RelaxedMockK
    lateinit var passwordEncoder: PasswordEncoder

    @RelaxedMockK
    lateinit var eventPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var resetPasswordByToken: ResetPasswordByToken

    @BeforeEach
    fun setup() {
        clearMocks(eventPublisher)
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
            AppInputValidationException(emptyMap())
    }

    @Test
    fun `should throw AppInputValidationException when password does not follow the rules`() {
        val password = "abc"
        every {
            generalValidator.matches(password, PASSWORD_REGEX, PASSWORD_FIELD_NAME, PASSWORD_ERROR_KEY)
        } returns mapOf("password" to I18nMessage("key"))

        assertThrows<AppInputValidationException> {
            resetPasswordByToken.reset(UUID.randomUUID().toString(), password)
        }
    }

    @Test
    fun `should throw ResetPasswordTokenFormatIsInvalidException when token format is invalid`() {
        every { securityPersistence.getPasswordResetToken(any()) } returns null
        assertThrows<ResetPasswordTokenFormatIsInvalidException> {
            resetPasswordByToken.reset(UUID.randomUUID().toString().plus("a"), validPassword)
        }
    }

    @Test
    fun `should throw ResetPasswordTokenIsInvalidException when token does not exist`() {
        every { securityPersistence.getPasswordResetToken(any()) } returns null
        assertThrows<ResetPasswordTokenIsInvalidException> {
            resetPasswordByToken.reset(UUID.randomUUID().toString(), validPassword)
        }
    }

    @Test
    fun `should throw ResetPasswordTokenIsExpiredException when token is expired`() {
        every { securityPersistence.getPasswordResetToken(any()) } returns
            PasswordResetToken(
                userSummary(1L, 1L), validToken,
                Instant.now().minusSeconds(RESET_PASSWORD_TOKEN_VALIDITY_PERIOD_IN_HOUR * 60 * 60)
            )
        assertThrows<ResetPasswordTokenIsExpiredException> {
            resetPasswordByToken.reset(validToken.toString(), validPassword)
        }
    }

    @Test
    fun `should throw ResetPasswordTokenIsInvalidException when user related to token does not exist`() {
        every { securityPersistence.getPasswordResetToken(any()) } returns PasswordResetToken(
            user, validToken, Instant.now()
        )
        every { userPersistence.getByEmail(user.email) } returns null
        assertThrows<ResetPasswordTokenIsInvalidException> {
            resetPasswordByToken.reset(validToken.toString(), validPassword)
        }
    }

    @Test
    fun `should update user password when token, user and password are valid`() {
        every { securityPersistence.getPasswordResetToken(any()) } returns PasswordResetToken(
            user, validToken, Instant.now()
        )
        every { userPersistence.getByEmail(user.email) } returns userWithPassword
        every { userPersistence.updatePassword(user.id, any()) } returns Unit
        every { securityPersistence.deletePasswordResetToken(validToken) } returns Unit

        resetPasswordByToken.reset(validToken.toString(), validPassword)

        verify(exactly = 1) { eventPublisher.publishEvent(PasswordWasResetByTokenEvent(userWithPassword)) }
    }
}
