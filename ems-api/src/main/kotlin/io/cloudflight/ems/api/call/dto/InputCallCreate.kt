package io.cloudflight.ems.api.call.dto

import io.cloudflight.ems.api.call.validator.UniqueCallName
import io.cloudflight.ems.api.programme.dto.ProgrammeObjectivePolicy
import java.time.ZonedDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class InputCallCreate (

    @field:NotBlank
    @field:Size(max = 150, message = "call.name.wrong.size")
    @UniqueCallName
    val name: String?,

    val priorityPolicies: Set<ProgrammeObjectivePolicy>?,

    @field:NotNull(message = "call.startDate.unknown")
    val startDate: ZonedDateTime?,

    @field:NotNull(message = "call.endDate.unknown")
    val endDate: ZonedDateTime?,

    @field:Size(max = 1000, message = "call.description.wrong.size")
    val description: String? = null

)
