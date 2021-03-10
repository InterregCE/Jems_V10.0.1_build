package io.cloudflight.jems.api.call.dto

import io.cloudflight.jems.api.common.validator.StartDateBeforeEndDate
import io.cloudflight.jems.api.call.validator.UniqueCallName
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.time.ZonedDateTime
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@StartDateBeforeEndDate("call.endDate.is.before.startDate")
data class InputCallCreate(

    @field:NotBlank
    @field:Size(max = 250, message = "call.name.wrong.size")
    @UniqueCallName
    val name: String?,

    val priorityPolicies: Set<ProgrammeObjectivePolicy>? = null,

    val strategies: Set<ProgrammeStrategy>? = null,

    @field:NotNull
    val isAdditionalFundAllowed: Boolean,

    val funds: Set<Long>? = null,

    @field:NotNull(message = "call.startDate.unknown")
    val startDate: ZonedDateTime?,

    @field:NotNull(message = "call.endDate.unknown")
    val endDate: ZonedDateTime?,

    val description: Set<InputTranslation> = emptySet(),

    @field:Min(1, message = "call.lengthOfPeriod.invalid")
    @field:Max(99, message = "call.lengthOfPeriod.invalid")
    val lengthOfPeriod: Int

)
