package io.cloudflight.jems.server.user.controller

import io.cloudflight.jems.api.user.UserRegistrationApi
import io.cloudflight.jems.api.user.dto.InputUserRegistration
import io.cloudflight.jems.api.user.dto.OutputUserWithRole
import io.cloudflight.jems.server.user.service.UserService
import org.springframework.web.bind.annotation.RestController

@RestController
class UserRegistrationController(
    val userService: UserService
) : UserRegistrationApi {

    override fun registerApplicant(user: InputUserRegistration): OutputUserWithRole {
        return userService.registerApplicant(user)
    }

}
