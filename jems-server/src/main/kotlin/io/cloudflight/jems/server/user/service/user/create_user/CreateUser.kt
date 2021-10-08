package io.cloudflight.jems.server.user.service.user.create_user

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.config.AppSecurityProperties
import io.cloudflight.jems.server.user.service.authorization.CanCreateUser
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserChange
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.user.validatePassword
import io.cloudflight.jems.server.user.service.user.validateUserCommon
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateUser(
    private val persistence: UserPersistence,
    private val appSecurityProperties: AppSecurityProperties,
    private val passwordEncoder: PasswordEncoder,
    private val generalValidator: GeneralValidatorService,
    private val eventPublisher: ApplicationEventPublisher
) : CreateUserInteractor {

    @CanCreateUser
    @Transactional
    @ExceptionWrapper(CreateUserException::class)
    override fun createUser(user: UserChange): User {
        validateUser(user)

        val password = getDefaultPasswordFromEmail(user.email)
        validatePassword(generalValidator, password)

        if (user.userStatus == UserStatus.UNCONFIRMED) {
            // todo here we should generate confirmation token and pass it to the persistence
        }

        return persistence.create(user = user, passwordEncoded = passwordEncoder.encode(password)).also {
            eventPublisher.publishEvent(UserCreatedEvent(it))
        }
    }

    private fun validateUser(user: UserChange) {
        if (user.id != 0L)
            throw UserIdCannotBeSpecified()

        validateUserCommon(generalValidator, user)

        if (!persistence.userRoleExists(user.userRoleId))
            throw UserRoleNotFound()
        if (persistence.emailExists(user.email))
            throw UserEmailAlreadyTaken()
    }

    private fun getDefaultPasswordFromEmail(email: String): String =
        appSecurityProperties.defaultPasswordPrefix.plus(email)

}
