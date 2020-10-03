package io.cloudflight.jems.server.controller

import io.cloudflight.jems.api.AuthenticationApi
import io.cloudflight.jems.api.dto.LoginRequest
import io.cloudflight.jems.api.user.dto.OutputCurrentUser
import io.cloudflight.jems.server.security.service.AuthenticationService
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest


@RestController
class AuthenticationController(
    private val authenticationService: AuthenticationService
) : AuthenticationApi {

    override fun getCurrentUser(): OutputCurrentUser? {
        return authenticationService.getCurrentUser();
    }

    override fun login(req: HttpServletRequest, loginRequest: LoginRequest): OutputCurrentUser? {
        return authenticationService.login(req, loginRequest);
    }

    override fun logout(req: HttpServletRequest) {
        authenticationService.logout(req);
    }
}
