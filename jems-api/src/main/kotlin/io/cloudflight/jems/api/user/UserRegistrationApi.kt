package io.cloudflight.jems.api.user

import io.cloudflight.jems.api.user.dto.UserRegistrationDTO
import io.cloudflight.jems.api.user.dto.UserDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Api("User Registration")
@RequestMapping("/api/registration")
interface UserRegistrationApi {

    @ApiOperation("Creates new User with default user role")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun registerApplicant(@RequestBody user: UserRegistrationDTO): UserDTO

}
