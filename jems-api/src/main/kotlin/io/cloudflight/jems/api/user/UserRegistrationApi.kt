package io.cloudflight.jems.api.user

import io.cloudflight.jems.api.user.dto.CaptchaDTO
import io.cloudflight.jems.api.user.dto.UserRegistrationDTO
import io.cloudflight.jems.api.user.dto.UserDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@Api("User Registration")
interface UserRegistrationApi {

    companion object {
        private const val ENDPOINT_API_USER_REGISTRATION = "/api/registration"
    }

    @ApiOperation("Creates new User with default user role")
    @PostMapping(ENDPOINT_API_USER_REGISTRATION, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun registerApplicant(@RequestBody user: UserRegistrationDTO): UserDTO

    @ApiOperation("Confirm user registration")
    @GetMapping(ENDPOINT_API_USER_REGISTRATION)
    fun confirmUserRegistration(@RequestParam(required = true) token: UUID): Boolean
}
