package io.cloudflight.jems.server.authentication

import io.cloudflight.jems.server.authentication.service.LoginAttemptService
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Component
class AuthenticationSuccessListener(val loginAttemptService: LoginAttemptService) :
    ApplicationListener<AuthenticationSuccessEvent?> {

    @Transactional
    override fun onApplicationEvent(event: AuthenticationSuccessEvent) =
        loginAttemptService.deleteFailedLoginAttempt(event.authentication.name)
}
