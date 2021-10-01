package io.cloudflight.jems.server.user.service.user.register_user

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.userrole.ProgrammeDataPersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserChange
import io.cloudflight.jems.server.user.service.model.UserRegistration
import io.cloudflight.jems.server.user.service.user.validatePassword
import io.cloudflight.jems.server.user.service.user.validateUserCommon
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterUser(
    private val persistence: UserPersistence,
    private val programmeDataPersistence: ProgrammeDataPersistence,
    private val passwordEncoder: PasswordEncoder,
    private val eventPublisher: ApplicationEventPublisher,
    private val generalValidator: GeneralValidatorService,
) : RegisterUserInteractor {

    @Transactional
    @ExceptionWrapper(RegisterUserException::class)
    override fun registerUser(user: UserRegistration): User {
        val userRoleId = programmeDataPersistence.getDefaultUserRole()
        if (userRoleId == null || !persistence.userRoleExists(userRoleId))
            throw DefaultUserRoleNotFound()
        val userToBeRegistered = user.toUserChange(userRoleId)

        validateUser(userToBeRegistered)
        validatePassword(generalValidator, user.password)

        return persistence.create(user = userToBeRegistered, passwordEncoded = passwordEncoder.encode(user.password))
            .also {
                eventPublisher.publishEvent(UserRegisteredEvent(it))
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
