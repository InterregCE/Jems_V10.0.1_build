package io.cloudflight.ems.api

import io.cloudflight.ems.api.dto.LoginRequest
import io.cloudflight.ems.api.dto.OutputCurrentUser
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@Api("Authentication")
@RequestMapping("/api/auth")
interface AuthenticationApi {

    @ApiOperation("Returns the current user")
    @GetMapping("/current")
    fun getCurrentUser(): OutputCurrentUser

    @ApiOperation("Logs in the user with the given credentials")
    @PostMapping("/login", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun login(req: HttpServletRequest, @Valid @RequestBody loginRequest: LoginRequest): OutputCurrentUser

    @ApiOperation("Logs out the current user")
    @PostMapping("/logout")
    @ResponseStatus(value = HttpStatus.OK)
    fun logout(req: HttpServletRequest)
}
