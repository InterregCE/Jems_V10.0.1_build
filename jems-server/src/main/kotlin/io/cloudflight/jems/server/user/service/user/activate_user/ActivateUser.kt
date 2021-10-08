package io.cloudflight.jems.server.user.service.user.activate_user

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.mail.confirmation.service.MailConfirmationPersistence
import io.cloudflight.jems.server.mail.confirmation.service.model.MailConfirmation
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.model.UserChange
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserWithPassword
import io.cloudflight.jems.server.user.service.user.register_user.DefaultUserRoleNotFound
import io.cloudflight.jems.server.user.service.user.register_user.RegisterUserException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class ActivateUser(
    private val confirmationMailPersistence: MailConfirmationPersistence,
    private val userPersistence: UserPersistence
) : ActivateUserInteractor {

    @Transactional
    @ExceptionWrapper(ActivateUserException::class)
    override fun activateUser(token: UUID): Boolean {
        val confirmation = confirmationMailPersistence.getByToken(token)
        if (confirmation.clicked)
            throw UserAlreadyActive() // already clicked
        if (ChronoUnit.DAYS.between(confirmation.timestamp, ZonedDateTime.now()) > 6)
            throw LinkExpired() // older than 7 days

        // get user
        val user = userPersistence.getById(confirmation.userId)

        // activate user
        userPersistence.update(user.toUserChange())

        // update confirmation to be clicked
        confirmationMailPersistence.save(MailConfirmation(
            token = confirmation.token,
            userId = confirmation.userId,
            timestamp = confirmation.timestamp,
            clicked = true
        ))

        return true
    }

    private fun UserWithPassword.toUserChange() = UserChange(
        id = id,
        email = email,
        name = name,
        surname = surname,
        userRoleId = userRole.id,
        userStatus = UserStatus.ACTIVE
    )
}
