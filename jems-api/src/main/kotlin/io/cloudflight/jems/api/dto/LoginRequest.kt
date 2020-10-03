package io.cloudflight.jems.api.dto

import javax.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "login.email.should.not.be.empty")
    val email: String,

    @field:NotBlank(message = "login.password.should.not.be.empty")
    val password: String
)
