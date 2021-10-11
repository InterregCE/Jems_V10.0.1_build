package io.cloudflight.jems.server.user.service.user.update_user

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.authorization.CanUpdateUser
import io.cloudflight.jems.server.user.service.confirmation.UserConfirmationPersistence
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserChange
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.user.validateUserCommon
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateUser(
    private val persistence: UserPersistence,
    private val generalValidator: GeneralValidatorService,
    private val eventPublisher: ApplicationEventPublisher,
    private val userConfirmationPersistence: UserConfirmationPersistence
) : UpdateUserInteractor {

    @CanUpdateUser
    @Transactional
    @ExceptionWrapper(UpdateUserException::class)
    override fun updateUser(user: UserChange): User {
        val oldUser = persistence.getById(user.id).getUser()
        validateUser(oldUser = oldUser, newUser = user)

        return persistence.update(user).also {
            val confirmationToken =
                if (oldUser.userStatus != UserStatus.UNCONFIRMED && it.userStatus == UserStatus.UNCONFIRMED)
                    userConfirmationPersistence.createNewConfirmation(it.id).token.toString()
                else null
            eventPublisher.publishEvent(UserUpdatedEvent(it, oldUser, confirmationToken))
        }
    }

    private fun validateUser(oldUser: User, newUser: UserChange) {
        validateUserCommon(generalValidator, newUser)
        if (oldUser.email != newUser.email && persistence.emailExists(newUser.email))
            throw UserEmailAlreadyTaken()
        if (oldUser.userRole.id != newUser.userRoleId && !persistence.userRoleExists(newUser.userRoleId))
            throw UserRoleNotFound()
    }

}
