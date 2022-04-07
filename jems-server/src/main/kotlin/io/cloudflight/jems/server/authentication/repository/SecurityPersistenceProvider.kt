package io.cloudflight.jems.server.authentication.repository

import io.cloudflight.jems.server.authentication.entity.PasswordResetTokenId
import io.cloudflight.jems.server.authentication.model.PasswordResetToken
import io.cloudflight.jems.server.authentication.service.SecurityPersistence
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.repository.user.toUserSummary
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
class SecurityPersistenceProvider(
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val userRepository: UserRepository
) : SecurityPersistence {

    @Transactional
    override fun savePasswordResetToken(passwordResetToken: PasswordResetToken) {
        passwordResetTokenRepository.save(
            passwordResetToken.toEntity(
                PasswordResetTokenId(userRepository.getById(passwordResetToken.user.id))
            )
        )
    }

    @Transactional(readOnly = true)
    override fun getPasswordResetToken(token: UUID): PasswordResetToken? =
        passwordResetTokenRepository.findByToken(token)?.let {
            it.toModel(it.id.user.toUserSummary())
        }

    @Transactional
    override fun deletePasswordResetToken(token: UUID) {
        passwordResetTokenRepository.deleteByToken(token)
    }
}
