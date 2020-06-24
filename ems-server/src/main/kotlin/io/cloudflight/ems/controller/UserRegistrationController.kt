package io.cloudflight.ems.controller

import io.cloudflight.ems.api.UserRegistrationApi
import io.cloudflight.ems.api.dto.user.InputUserRegistration
import io.cloudflight.ems.api.dto.user.OutputUser
import io.cloudflight.ems.service.UserService
import org.springframework.web.bind.annotation.RestController

@RestController
class UserRegistrationController(
    val userService: UserService
) : UserRegistrationApi {

    override fun registerApplicant(user: InputUserRegistration): OutputUser {
        return userService.registerApplicant(user)
    }

}
