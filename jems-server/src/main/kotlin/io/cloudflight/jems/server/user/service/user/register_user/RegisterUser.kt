package io.cloudflight.jems.server.user.service.user.register_user

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserChange
import io.cloudflight.jems.server.user.service.model.UserRegistration
import io.cloudflight.jems.server.user.service.user.validatePassword
import io.cloudflight.jems.server.user.service.user.validateUserCommon
import io.cloudflight.jems.server.user.service.userRegistered
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterUser(
    private val persistence: UserPersistence,
    private val passwordEncoder: PasswordEncoder,
    private val auditPublisher: ApplicationEventPublisher,
    private val generalValidator: GeneralValidatorService,
) : RegisterUserInteractor {

    companion object {
        private const val USER_ROLE_APPLICANT_ID = 3L
    }

    @Transactional
    @ExceptionWrapper(RegisterUserException::class)
    override fun registerUser(user: UserRegistration): User {
        if(!persistence.userRoleExists(USER_ROLE_APPLICANT_ID))
            throw UserRoleNotFound()

        val userToBeRegistered = user.toUserChange(USER_ROLE_APPLICANT_ID)

        validateUser(userToBeRegistered)
        validatePassword(generalValidator, user.password)

        return persistence.create(user = userToBeRegistered, passwordEncoded = passwordEncoder.encode(user.password)).also {
            auditPublisher.publishEvent(userRegistered(this, it))
        }
    }

    private fun validateUser(user: UserChange) {
        validateUserCommon(generalValidator, user)
        if (persistence.emailExists(user.email))
            throw UserEmailAlreadyTaken()
    }

    private fun UserRegistration.toUserChange(roleId: Long) = UserChange(
        id = 0,
        email = email,
        name = name,
        surname = surname,
        userRoleId = roleId,
    )

}
