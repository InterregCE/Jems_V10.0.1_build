package io.cloudflight.jems.server.authentication

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.LoginAttemptService
import io.cloudflight.jems.server.utils.loginEmail
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.event.AuthenticationSuccessEvent


internal class AuthenticationSuccessListenerTest : UnitTest() {
    @MockK
    lateinit var loginAttemptService: LoginAttemptService

    @MockK
    lateinit var event: AuthenticationSuccessEvent

    @InjectMockKs
    lateinit var authenticationSuccessListener: AuthenticationSuccessListener

    @Test
    fun `should delete failed login attempt when login is successful`() {
        every { loginAttemptService.deleteFailedLoginAttempt(loginEmail) } returns Unit
        every { event.authentication.name } returns loginEmail

        authenticationSuccessListener.onApplicationEvent(event)

        verify { loginAttemptService.deleteFailedLoginAttempt(loginEmail) }
    }
}
