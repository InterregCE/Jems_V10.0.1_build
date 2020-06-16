package io.cloudflight.ems.controller

import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.api.UserApi
import io.cloudflight.ems.api.dto.OutputUser
import io.cloudflight.ems.api.dto.OutputUserRole
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val securityService: SecurityService
) : UserApi {

    // TODO MP2-51 introduce security login
    override fun getCurrentUser(): OutputUser {
        return OutputUser(
            id = null,
            name = securityService.currentUser.user.name,
            surname = "",
            email = securityService.currentUser.user.username,
            userRole = OutputUserRole(-1, "dummy")
        )
    }
}
