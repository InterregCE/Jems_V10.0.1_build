package io.cloudflight.ems.api.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class InputUser (

    @field:NotBlank(message = "user.email.should.not.be.empty")
    @field:Size(max = 255, message = "user.email.wrong.size")
    @field:Email(message = "user.email.wrong.format")
    val email: String,

    @field:NotBlank(message = "user.name.should.not.be.empty")
    @field:Size(min = 2, max = 50, message = "user.name.wrong.size")
    val name: String,

    @field:NotBlank(message = "user.surname.should.not.be.empty")
    @field:Size(min = 2, max = 50, message = "user.surname.wrong.size")
    val surname: String,

    @field:NotNull(message = "user.accountRoleId.should.not.be.empty")
    val accountRoleId: Long?

)
