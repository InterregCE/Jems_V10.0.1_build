package io.cloudflight.jems.server.security.service

import io.cloudflight.jems.api.dto.LoginRequest
import io.cloudflight.jems.api.user.dto.OutputCurrentUser
import javax.servlet.http.HttpServletRequest

interface AuthenticationService {

    fun getCurrentUser(): OutputCurrentUser?
    fun login(req: HttpServletRequest, loginRequest: LoginRequest): OutputCurrentUser?
    fun logout(req: HttpServletRequest)
}
