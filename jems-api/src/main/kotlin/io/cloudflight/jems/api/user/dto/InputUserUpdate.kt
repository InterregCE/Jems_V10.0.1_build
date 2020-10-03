package io.cloudflight.jems.api.user.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class InputUserUpdate(

    @field:NotNull(message = "common.id.should.not.be.empty")
    val id: Long,

    @field:NotBlank(message = "user.email.should.not.be.empty")
    @field:Size(max = 255, message = "user.email.wrong.size")
    @field:Email(message = "user.email.wrong.format")
    val email: String,

    @field:Size(min = 1, max = 50, message = "user.name.wrong.size")
    val name: String,

    @field:Size(min = 1, max = 50, message = "user.surname.wrong.size")
    val surname: String,

    @field:NotNull(message = "user.userRoleId.should.not.be.empty")
    val userRoleId: Long

)
