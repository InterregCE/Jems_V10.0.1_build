package io.cloudflight.ems.controller

import io.cloudflight.ems.api.UserRegistrationApi
import io.cloudflight.ems.api.dto.user.InputUserRegistration
import io.cloudflight.ems.api.dto.user.OutputUserWithRole
import io.cloudflight.ems.service.UserService
import org.springframework.web.bind.annotation.RestController

@RestController
class UserRegistrationController(
    val userService: UserService
) : UserRegistrationApi {

    override fun registerApplicant(user: InputUserRegistration): OutputUserWithRole {
        return userService.registerApplicant(user)
    }

}
