package io.cloudflight.ems.api.dto.user

import javax.validation.constraints.Size

data class InputPassword (

    @field:Size(min = 10, message = "user.password.size.too.short")
    val password: String

)
