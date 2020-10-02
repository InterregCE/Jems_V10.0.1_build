package io.cloudflight.ems.user.controller

import io.cloudflight.ems.api.user.UserRegistrationApi
import io.cloudflight.ems.api.user.dto.InputUserRegistration
import io.cloudflight.ems.api.user.dto.OutputUserWithRole
import io.cloudflight.ems.user.service.UserService
import org.springframework.web.bind.annotation.RestController

@RestController
class UserRegistrationController(
    val userService: UserService
) : UserRegistrationApi {

    override fun registerApplicant(user: InputUserRegistration): OutputUserWithRole {
        return userService.registerApplicant(user)
    }

}
