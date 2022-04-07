package io.cloudflight.jems.server.authentication.service.resetPasswordByToken

import io.cloudflight.jems.server.authentication.model.PasswordResetToken
import io.cloudflight.jems.server.authentication.service.SecurityPersistence
import io.cloudflight.jems.server.authentication.service.emailPasswordResetLink.RESET_PASSWORD_TOKEN_VALIDITY_PERIOD_IN_HOUR
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.user.validatePassword
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class ResetPasswordByToken(
    private val userPersistence: UserPersistence,
    private val securityPersistence: SecurityPersistence,
    private val generalValidator: GeneralValidatorService,
    private val passwordEncoder: PasswordEncoder,
    private val eventPublisher: ApplicationEventPublisher
) : ResetPasswordByTokenInteractor {

    @Transactional
    @ExceptionWrapper(ResetPasswordByTokenException::class)
    override fun reset(token: String, newPassword: String) {
        validatePassword(generalValidator, newPassword)
        securityPersistence.getPasswordResetToken(getUUIDorThrow(token))?.let { passwordResetToken ->
            throwIfTokenIsExpired(passwordResetToken)
            userPersistence.getByEmail(passwordResetToken.user.email)?.let { user ->
                userPersistence.updatePassword(user.id, passwordEncoder.encode(newPassword))
                securityPersistence.deletePasswordResetToken(passwordResetToken.token)
                eventPublisher.publishEvent(PasswordWasResetByTokenEvent(user))
            } ?: throw ResetPasswordTokenIsInvalidException()
        } ?: throw ResetPasswordTokenIsInvalidException()
    }

    private fun throwIfTokenIsExpired(passwordResetToken: PasswordResetToken) {
        if (passwordResetToken.generatedAt.isBefore(
                Instant.now().minusSeconds(RESET_PASSWORD_TOKEN_VALIDITY_PERIOD_IN_HOUR * 60 * 60)
            )
        ) throw ResetPasswordTokenIsExpiredException()
    }

    private fun getUUIDorThrow(token: String): UUID =
        runCatching {
            UUID.fromString(token)
        }.onFailure {
            throw ResetPasswordTokenFormatIsInvalidException()
        }.getOrThrow()

}
