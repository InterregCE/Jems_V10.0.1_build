package io.cloudflight.skeleton.controller

import io.cloudflight.skeleton.security.service.SecurityService
import io.cloudflight.skeleton.angular.api.UserApi
import io.cloudflight.skeleton.angular.api.dto.OutputUser
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val securityService: SecurityService
) : UserApi {

    override fun getCurrentUser(): OutputUser {
        return OutputUser(securityService.currentUser.user.name)
    }
}
