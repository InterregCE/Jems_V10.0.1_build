package io.cloudflight.jems.api.authentication

import io.cloudflight.jems.api.authentication.dto.LoginRequest
import io.cloudflight.jems.api.authentication.dto.OutputCurrentUser
import io.cloudflight.jems.api.authentication.dto.ResetPasswordByTokenRequestDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@Api("Authentication")
interface AuthenticationApi {

    companion object {
        private const val ENDPOINT_API_AUTH = "/api/auth"
    }

    @ApiOperation("Returns the current user")
    @GetMapping("$ENDPOINT_API_AUTH/current")
    fun getCurrentUser(): OutputCurrentUser?

    @ApiOperation("Logs in the user with the given credentials")
    @PostMapping("$ENDPOINT_API_AUTH/login", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun login(req: HttpServletRequest, @Valid @RequestBody loginRequest: LoginRequest): OutputCurrentUser?

    @ApiOperation("Logs out the current user")
    @PostMapping("$ENDPOINT_API_AUTH/logout")
    @ResponseStatus(value = HttpStatus.OK)
    fun logout(req: HttpServletRequest)

    @ApiOperation("send password reset link to email address")
    @PostMapping("$ENDPOINT_API_AUTH/password/resetLink",  consumes = [MediaType.TEXT_PLAIN_VALUE])
    fun sendPasswordResetLinkToEmail(@RequestBody email: String)

    @ApiOperation("reset password")
    @PostMapping("$ENDPOINT_API_AUTH/password/reset",  consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun resetPassword(@RequestBody resetPasswordByTokenRequest: ResetPasswordByTokenRequestDTO)
}
