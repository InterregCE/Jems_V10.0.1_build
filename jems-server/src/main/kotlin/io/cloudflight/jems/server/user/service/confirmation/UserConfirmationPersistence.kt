package io.cloudflight.jems.server.user.service.confirmation

import io.cloudflight.jems.server.user.service.model.UserConfirmation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

interface UserConfirmationPersistence {
    fun createNewConfirmation(userId: Long): UserConfirmation

    fun save(confirmation: UserConfirmation): UserConfirmation

    fun getByToken(token: UUID): UserConfirmation
}
