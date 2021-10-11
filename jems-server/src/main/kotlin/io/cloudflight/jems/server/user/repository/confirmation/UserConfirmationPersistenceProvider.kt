package io.cloudflight.jems.server.user.repository.confirmation

import io.cloudflight.jems.server.user.entity.UserConfirmationEntity
import io.cloudflight.jems.server.user.service.confirmation.UserConfirmationPersistence
import io.cloudflight.jems.server.user.service.model.UserConfirmation
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.*

@Service
class UserConfirmationPersistenceProvider(
    private val repository: UserConfirmationRepository
) : UserConfirmationPersistence {

    @Transactional
    override fun createNewConfirmation(userId: Long): UserConfirmation =
        repository.save(
            UserConfirmationEntity(
                token = UUID.randomUUID(),
                userId = userId,
                timestamp = ZonedDateTime.now(),
                confirmed = false
            )
        ).toModel()

    @Transactional
    override fun save(confirmation: UserConfirmation): UserConfirmation =
        repository.save(confirmation.toEntity()).toModel()

    @Transactional
    override fun getByToken(token: UUID): UserConfirmation =
        repository.findByToken(token).toModel()
}
