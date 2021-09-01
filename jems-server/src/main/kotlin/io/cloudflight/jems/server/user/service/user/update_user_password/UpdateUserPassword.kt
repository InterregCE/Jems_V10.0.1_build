package io.cloudflight.jems.server.user.service.user.update_user_password

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.authorization.CanUpdateUserPassword
import io.cloudflight.jems.server.user.service.model.Password
import io.cloudflight.jems.server.user.service.model.UserWithPassword
import io.cloudflight.jems.server.user.service.passwordChanged
import io.cloudflight.jems.server.user.service.user.validatePassword
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateUserPassword(
    private val persistence: UserPersistence,
    private val securityService: SecurityService,
    private val passwordEncoder: PasswordEncoder,
    private val generalValidator: GeneralValidatorService,
    private val auditPublisher: ApplicationEventPublisher,
) : UpdateUserPasswordInteractor {

    @CanUpdateUserPassword
    @Transactional
    @ExceptionWrapper(UpdateUserPasswordException::class)
    override fun resetUserPassword(userId: Long, newPassword: String) {
        validatePassword(generalValidator, newPassword)

        val user = persistence.getById(userId)

        persistence.updatePassword(userId, passwordEncoder.encode(newPassword)).also {
            auditPublisher.publishEvent(passwordChanged(this, user, initiator = securityService.currentUser!!.user))
        }
    }

    @Transactional
    @ExceptionWrapper(UpdateUserPasswordException::class)
    override fun updateMyPassword(passwordData: Password) {
        validatePassword(generalValidator, passwordData.password)

        val user = persistence.getById(securityService.currentUser!!.user.id)

        updatePasswordIfOldPasswordMatches(user, passwordData).also {
            auditPublisher.publishEvent(passwordChanged(this, user))
        }
    }

    private fun updatePasswordIfOldPasswordMatches(user: UserWithPassword, passwordData: Password) {
        if (!passwordEncoder.matches(passwordData.oldPassword, user.encodedPassword))
            throw UserOldPasswordDoesNotMatch()

        persistence.updatePassword(user.id, passwordEncoder.encode(passwordData.password))
    }

}
