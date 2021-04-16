package io.cloudflight.jems.server.call.service.model

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.time.ZonedDateTime

data class Call (
    val id: Long = 0,
    val name: String,
    var status: CallStatus? = null,
    val startDate: ZonedDateTime,
    val endDateStep1: ZonedDateTime? = null,
    val endDate: ZonedDateTime,
    val isAdditionalFundAllowed: Boolean,
    val lengthOfPeriod: Int,
    val description: Set<InputTranslation> = emptySet(),
    val priorityPolicies: Set<ProgrammeObjectivePolicy> = emptySet(),
    val strategies: Set<ProgrammeStrategy> = emptySet(),
    val fundIds: Set<Long> = emptySet(),
) {
    fun is2StepProcedureEnabled() = endDateStep1 != null
}
