package io.cloudflight.jems.server.authentication

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.AuthenticationServiceImpl.Companion.LOGIN_BLOCK_DURATION_IN_SECONDS
import io.cloudflight.jems.server.authentication.service.LoginAttemptService
import io.cloudflight.jems.server.utils.failedLoginAttemptEntity
import io.cloudflight.jems.server.utils.loginEmail
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent
import java.time.Instant

internal class AuthenticationFailureListenerTest : UnitTest(){

    @MockK
    lateinit var loginAttemptService: LoginAttemptService
    @MockK
    lateinit var event : AuthenticationFailureBadCredentialsEvent

    @InjectMockKs
    lateinit var authenticationFailureListener: AuthenticationFailureListener

    @Test
    fun `should increase count by one and update lastAttemptAt when attempts are consecutive`(){
        val failedLoginAttemptEntity = failedLoginAttemptEntity()
        val oldCount = failedLoginAttemptEntity.count
        val oldLastAttemptAt = failedLoginAttemptEntity.lastAttemptAt
        every { loginAttemptService.getFailedLoginAttempt(loginEmail) } returns failedLoginAttemptEntity
        every { event.authentication.name } returns loginEmail

        authenticationFailureListener.onApplicationEvent(event)

        assertThat(failedLoginAttemptEntity.count).isEqualTo(oldCount.inc())
        assertThat(failedLoginAttemptEntity.lastAttemptAt).isAfter(oldLastAttemptAt)
    }

    @Test
    fun `should reset count to one and update lastAttemptAt when attempt occur after login block duration has passed`(){
        val failedLoginAttemptEntity = failedLoginAttemptEntity(lastAttemptAt = Instant.now()
            .minusSeconds(LOGIN_BLOCK_DURATION_IN_SECONDS.toLong())
            .minusSeconds(1)
        )
        val oldLastAttemptAt = failedLoginAttemptEntity.lastAttemptAt
        every { loginAttemptService.getFailedLoginAttempt(loginEmail) } returns failedLoginAttemptEntity
        every { event.authentication.name } returns loginEmail

        authenticationFailureListener.onApplicationEvent(event)

        assertThat(failedLoginAttemptEntity.count).isEqualTo(1)
        assertThat(failedLoginAttemptEntity.lastAttemptAt).isAfter(oldLastAttemptAt)
    }

    @Test
    fun `should save the failed login attempt when it is the first failed login attempt`(){

        val countSlot = slot<Short>()
        val lastAttemptAtSlot = slot<Instant>()
        every { loginAttemptService.getFailedLoginAttempt(loginEmail) } returns null
        every { loginAttemptService.saveFailedLoginAttempt(loginEmail,capture(countSlot), capture(lastAttemptAtSlot) ) } returns Unit
        every { event.authentication.name } returns loginEmail

        authenticationFailureListener.onApplicationEvent(event)

        assertThat(countSlot.captured).isEqualTo(1)

        verify { loginAttemptService.saveFailedLoginAttempt(loginEmail,countSlot.captured, lastAttemptAtSlot.captured) }
    }
}
