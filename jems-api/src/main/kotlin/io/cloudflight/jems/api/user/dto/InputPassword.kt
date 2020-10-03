package io.cloudflight.jems.api.user.dto

import javax.validation.constraints.Size

data class InputPassword (

    @field:Size(min = 10, message = "user.password.size.too.short")
    val password: String,

    val oldPassword: String? = null

)
