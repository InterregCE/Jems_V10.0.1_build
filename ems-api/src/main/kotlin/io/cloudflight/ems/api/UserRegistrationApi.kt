package io.cloudflight.ems.api

import io.cloudflight.ems.api.dto.user.InputUserRegistration
import io.cloudflight.ems.api.dto.user.OutputUser
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Api("User Registration")
@RequestMapping("/api/registration")
interface UserRegistrationApi {

    @ApiOperation("Creates new User with applicant role")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun registerApplicant(@Valid @RequestBody user: InputUserRegistration): OutputUser

}
