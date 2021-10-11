package io.cloudflight.jems.server.user.service.user.activate_user

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.user.service.confirmation.UserConfirmationPersistence
import io.cloudflight.jems.server.user.service.model.UserConfirmation
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.model.UserChange
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserWithPassword
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class ActivateUser(
    private val userConfirmationPersistence: UserConfirmationPersistence,
    private val userPersistence: UserPersistence
) : ActivateUserInteractor {

    @Transactional
    @ExceptionWrapper(ActivateUserException::class)
    override fun activateUser(token: UUID): Boolean {
        val confirmation = userConfirmationPersistence.getByToken(token)
        if (confirmation.confirmed)
            throw UserAlreadyActive() // already clicked
        if (ChronoUnit.DAYS.between(confirmation.timestamp, ZonedDateTime.now()) > 6)
            throw LinkExpired() // older than 7 days

        // get user
        val user = userPersistence.getById(confirmation.userId)

        // activate user
        userPersistence.update(user.toUserChange())

        // update confirmation to be clicked
        userConfirmationPersistence.save(
            UserConfirmation(
                token = confirmation.token,
                userId = confirmation.userId,
                timestamp = confirmation.timestamp,
                confirmed = true
            )
        )

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
