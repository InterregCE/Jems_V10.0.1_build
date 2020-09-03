package io.cloudflight.ems.api.call.dto

import io.cloudflight.ems.api.validators.StartDateBeforeEndDate
import io.cloudflight.ems.api.programme.dto.ProgrammeObjectivePolicy
import java.time.ZonedDateTime
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@StartDateBeforeEndDate("call.endDate.is.before.startDate")
data class InputCallUpdate (

    @field:NotNull(message = "common.id.should.not.be.empty")
    val id: Long,

    @field:NotBlank
    @field:Size(max = 150, message = "call.name.wrong.size")
    val name: String?,

    @field:NotNull(message = "call.priorityPolicies.unknown")
    val priorityPolicies: Set<ProgrammeObjectivePolicy>?,

    @field:NotNull(message = "call.startDate.unknown")
    val startDate: ZonedDateTime?,

    @field:NotNull(message = "call.endDate.unknown")
    val endDate: ZonedDateTime?,

    @field:Size(max = 1000, message = "call.description.wrong.size")
    val description: String? = null,

    @field:Min(1, message = "call.lengthOfPeriod.invalid")
    @field:Max(99, message = "call.lengthOfPeriod.invalid")
    val lengthOfPeriod: Int? = null

)
