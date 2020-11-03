package io.cloudflight.jems.server.authentication.service

import io.cloudflight.jems.api.authentication.dto.LoginRequest
import io.cloudflight.jems.api.authentication.dto.OutputCurrentUser
import javax.servlet.http.HttpServletRequest

interface AuthenticationService {

    fun getCurrentUser(): OutputCurrentUser
    fun login(req: HttpServletRequest, loginRequest: LoginRequest): OutputCurrentUser?
    fun logout(req: HttpServletRequest)
}
