package io.cloudflight.jems.server.authentication.service

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.utils.loginEmail
import io.cloudflight.jems.server.utils.failedLoginAttemptEntity
import io.cloudflight.jems.server.user.entity.FailedLoginAttemptEntity
import io.cloudflight.jems.server.user.repository.user.FailedLoginAttemptRepository
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.utils.userEntityOfLogin
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant


internal class LoginAttemptServiceImplTest : UnitTest() {

    @MockK
    lateinit var repository: FailedLoginAttemptRepository

    @MockK
    lateinit var userRepository: UserRepository

    @InjectMockKs
    lateinit var loginAttemptService: LoginAttemptServiceImpl


    @Test
    fun `should return failed login attempt for the email`() {
        val failedLoginAttemptEntity = failedLoginAttemptEntity()
        every { repository.findByEmail(loginEmail) } returns failedLoginAttemptEntity

        assertThat(loginAttemptService.getFailedLoginAttempt(loginEmail)).isEqualTo(
            failedLoginAttemptEntity
        )
    }

    @Test
    fun `should save failed login attempt for the email`() {
        val entitySlot = slot<FailedLoginAttemptEntity>()
        val currentTime = Instant.now()
        val count = 1.toShort()

        every { userRepository.getOneByEmail(loginEmail) } returns userEntityOfLogin
        every { repository.save(capture(entitySlot)) } returnsArgument 0

        loginAttemptService.saveFailedLoginAttempt(loginEmail, count, currentTime)

        assertThat(entitySlot.captured.count).isEqualTo(count)
        assertThat(entitySlot.captured.lastAttemptAt).isEqualTo(currentTime)
    }

    @Test
    fun `should delete failed login attempt for the email`() {
        every { repository.deleteByEmail(loginEmail) } returns Unit

        loginAttemptService.deleteFailedLoginAttempt(loginEmail)

        verify { repository.deleteByEmail(loginEmail) }

    }
}
