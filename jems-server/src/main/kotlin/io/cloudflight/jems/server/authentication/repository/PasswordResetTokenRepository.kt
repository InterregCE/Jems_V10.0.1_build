package io.cloudflight.jems.server.authentication.repository

import io.cloudflight.jems.server.authentication.entity.PasswordResetTokenEntity
import io.cloudflight.jems.server.authentication.entity.PasswordResetTokenId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PasswordResetTokenRepository : JpaRepository<PasswordResetTokenEntity, PasswordResetTokenId> {
    fun findByToken(token : UUID) : PasswordResetTokenEntity?
    fun deleteByToken(token : UUID)
}
