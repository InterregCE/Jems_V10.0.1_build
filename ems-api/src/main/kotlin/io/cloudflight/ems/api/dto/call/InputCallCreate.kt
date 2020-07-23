package io.cloudflight.ems.api.dto.call

import java.time.LocalDate
import java.time.ZonedDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class InputCallCreate (

    @field:NotBlank
    @field:Size(max = 150, message = "user.name.wrong.size")
    val name: String,

    val startDate: ZonedDateTime,

    val endDate: ZonedDateTime,

    @field:Size(max = 1000, message = "user.name.wrong.size")
    val description: String?

)
