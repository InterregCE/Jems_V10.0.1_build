package io.cloudflight.jems.server.authentication.repository

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.entity.PasswordResetTokenEntity
import io.cloudflight.jems.server.authentication.entity.PasswordResetTokenId
import io.cloudflight.jems.server.authentication.model.PasswordResetToken
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.repository.user.toUserSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.Instant
import java.util.UUID

internal class SecurityPersistenceProviderTest : UnitTest() {

    companion object {
        val tokenUUID: UUID = UUID.randomUUID()
        val now: Instant = Instant.now()

        private val userEntity = UserEntity(
            id = 1L,
            email = "email",
            name = "name",
            surname = "surname",
            userRole = UserRoleEntity(2L, "name"),
            password = "pass",
            userStatus = UserStatus.ACTIVE
        )
        val pwResetToken = PasswordResetToken(userEntity.toUserSummary(), tokenUUID, now)
        val pwResetTokenEntity = PasswordResetTokenEntity(PasswordResetTokenId(userEntity), tokenUUID, now)
    }

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    private lateinit var passwordResetTokenRepository: PasswordResetTokenRepository

    @InjectMockKs
    private lateinit var persistence: SecurityPersistenceProvider

    @Test
    fun `save PasswordResetToken`() {
        val entitySlot = slot<PasswordResetTokenEntity>()
        every { userRepository.getById(userEntity.id) } returns userEntity
        every { passwordResetTokenRepository.save(capture(entitySlot)) } returns pwResetTokenEntity

        assertDoesNotThrow {
            persistence.savePasswordResetToken(pwResetToken)
        }
        assertThat(entitySlot.captured.token).isEqualTo(tokenUUID)
        assertThat(entitySlot.captured.id.user.id).isEqualTo(userEntity.id)
        assertThat(entitySlot.captured.id.user.email).isEqualTo(userEntity.email)
        assertThat(entitySlot.captured.generatedAt).isEqualTo(now)
    }

    @Test
    fun `get PasswordResetToken`() {
        every { passwordResetTokenRepository.findByToken(tokenUUID) } returns pwResetTokenEntity

        assertThat(persistence.getPasswordResetToken(tokenUUID)).isEqualTo(pwResetToken)
        verify { passwordResetTokenRepository.findByToken(tokenUUID) }
    }

    @Test
    fun `delete PasswordResetToken`() {
        every { passwordResetTokenRepository.deleteByToken(tokenUUID) } returns Unit

        assertDoesNotThrow {
            persistence.deletePasswordResetToken(tokenUUID)
        }
        verify { passwordResetTokenRepository.deleteByToken(tokenUUID) }
    }
}
