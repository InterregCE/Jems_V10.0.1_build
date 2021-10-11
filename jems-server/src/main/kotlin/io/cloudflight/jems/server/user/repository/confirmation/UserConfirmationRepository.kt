package io.cloudflight.jems.server.user.repository.confirmation

import io.cloudflight.jems.server.user.entity.UserConfirmationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserConfirmationRepository : JpaRepository<UserConfirmationEntity, Long> {
    fun findByToken(token: UUID): UserConfirmationEntity
}
