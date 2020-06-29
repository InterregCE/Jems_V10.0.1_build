package io.cloudflight.ems.api.dto.user

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class InputUserRegistration (

    @field:NotBlank(message = "user.email.should.not.be.empty")
    @field:Size(max = 255, message = "user.email.wrong.size")
    @field:Email(message = "user.email.wrong.format")
    val email: String,

    @field:Size(min = 1, max = 50, message = "user.name.wrong.size")
    val name: String,

    @field:Size(min = 1, max = 50, message = "user.surname.wrong.size")
    val surname: String,

    @field:Size(min = 10, message = "user.password.size.too.short")
    val password: String

)
