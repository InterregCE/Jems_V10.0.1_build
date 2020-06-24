package io.cloudflight.ems.controller

import io.cloudflight.ems.api.AuthenticationApi
import io.cloudflight.ems.api.dto.LoginRequest
import io.cloudflight.ems.api.dto.user.OutputCurrentUser
import io.cloudflight.ems.security.service.AuthenticationService
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
