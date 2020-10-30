package io.cloudflight.jems.server.authentication.controller

import io.cloudflight.jems.api.authentication.AuthenticationApi
import io.cloudflight.jems.api.authentication.dto.LoginRequest
import io.cloudflight.jems.api.authentication.dto.OutputCurrentUser
import io.cloudflight.jems.server.authentication.service.AuthenticationService
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
