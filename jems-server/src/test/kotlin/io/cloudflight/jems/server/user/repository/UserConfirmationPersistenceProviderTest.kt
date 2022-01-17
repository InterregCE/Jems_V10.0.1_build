package io.cloudflight.jems.server.user.repository

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.user.entity.UserConfirmationEntity
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.entity.UserRolePermissionEntity
import io.cloudflight.jems.server.user.entity.UserRolePermissionId
import io.cloudflight.jems.server.user.repository.confirmation.UserConfirmationPersistenceProvider
import io.cloudflight.jems.server.user.repository.confirmation.UserConfirmationRepository
import io.cloudflight.jems.server.user.repository.user.UserNotFound
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.repository.user.UserRoleNotFound
import io.cloudflight.jems.server.user.repository.userrole.UserRolePermissionRepository
import io.cloudflight.jems.server.user.repository.userrole.UserRoleRepository
import io.cloudflight.jems.server.user.service.confirmation.UserConfirmationPersistence
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserChange
import io.cloudflight.jems.server.user.service.model.UserConfirmation
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectSubmission
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime
import java.util.*

internal class UserConfirmationPersistenceProviderTest : UnitTest() {

    companion object {
        private val token = UUID.randomUUID()

        val confirmationEntity = UserConfirmationEntity(
            userId = 1,
            token = token,
            timestamp = ZonedDateTime.now(),
            confirmed = false
        )

        val confirmationModel = UserConfirmation(
            userId = 1,
            token = token,
            timestamp = ZonedDateTime.now(),
            confirmed = false
        )
    }

    @MockK
    lateinit var confirmationRepository: UserConfirmationRepository

    @InjectMockKs
    private lateinit var persistence: UserConfirmationPersistenceProvider

    @Test
    fun `create new confirmation`() {
        every { confirmationRepository.save(any()) }.returns(confirmationEntity)

        val confirmation = persistence.createNewConfirmation(1L)

        assertThat(confirmation.token).isEqualTo(confirmationModel.token)
    }

    @Test
    fun `save a confirmation`() {
        every { confirmationRepository.save(any()) }.returns(confirmationEntity)

        val confirmation = persistence.save(confirmationModel)

        assertThat(confirmation.token).isEqualTo(confirmationModel.token)
    }

    @Test
    fun `retrieve a confirmation`() {
        every { confirmationRepository.findByToken(any()) }.returns(confirmationEntity)

        persistence.getByToken(confirmationModel.token)

        verify { confirmationRepository.findByToken(confirmationModel.token) }
    }


}
