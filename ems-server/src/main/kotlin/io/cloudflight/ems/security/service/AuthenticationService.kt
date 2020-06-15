package io.cloudflight.ems.security.service

import io.cloudflight.ems.api.dto.LoginRequest
import io.cloudflight.ems.api.dto.OutputCurrentUser
import javax.servlet.http.HttpServletRequest

interface AuthenticationService {

    fun getCurrentUser(): OutputCurrentUser
    fun login(req: HttpServletRequest, loginRequest: LoginRequest): OutputCurrentUser
    fun logout(req: HttpServletRequest)
}
