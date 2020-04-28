package io.cloudflight.ems.controller

import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.api.UserApi
import io.cloudflight.ems.api.dto.OutputUser
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val securityService: SecurityService
) : UserApi {

    override fun getCurrentUser(): OutputUser {
        return OutputUser(securityService.currentUser.user.name)
    }
}
