package io.cloudflight.ems.controller

import io.cloudflight.ems.api.UserApi
import io.cloudflight.ems.api.dto.InputUser
import io.cloudflight.ems.api.dto.OutputUser
import io.cloudflight.ems.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    val userService: UserService
) : UserApi {

    override fun list(pageable: Pageable): Page<OutputUser> {
        return userService.findAll(pageable = pageable)
    }

    override fun createUser(user: InputUser): OutputUser {
        return userService.create(user)
    }
}
