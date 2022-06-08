package io.cloudflight.jems.server.authentication

import io.cloudflight.jems.server.authentication.service.AuthenticationServiceImpl.Companion.LOGIN_BLOCK_DURATION_IN_SECONDS
import io.cloudflight.jems.server.authentication.service.LoginAttemptService
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Component
class AuthenticationFailureListener(private val loginAttemptService: LoginAttemptService) :
    ApplicationListener<AuthenticationFailureBadCredentialsEvent?> {

    @Transactional
    override fun onApplicationEvent(event: AuthenticationFailureBadCredentialsEvent) {

        loginAttemptService.getFailedLoginAttempt(event.authentication.name)?.also { loginAttempt ->
            val currentTime = Instant.now()

            loginAttempt.count =
                if (loginBlockDurationHasPassed(currentTime, loginAttempt.lastAttemptAt)) 1
                else loginAttempt.count.inc()

            loginAttempt.lastAttemptAt = currentTime

        } ?: loginAttemptService.saveFailedLoginAttempt(event.authentication.name, 1, Instant.now())

    }

    private fun loginBlockDurationHasPassed(currentTime: Instant, lastAttempt: Instant) =
        lastAttempt.until(currentTime, ChronoUnit.SECONDS) > LOGIN_BLOCK_DURATION_IN_SECONDS

}
