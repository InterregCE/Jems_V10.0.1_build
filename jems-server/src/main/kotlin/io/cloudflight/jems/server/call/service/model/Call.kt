package io.cloudflight.jems.server.call.service.model

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.time.ZonedDateTime

data class Call(
    val id: Long = 0,
    val name: String,
    var status: CallStatus? = null,
    var type: CallType,
    var startDate: ZonedDateTime,
    var endDateStep1: ZonedDateTime? = null,
    var endDate: ZonedDateTime,
    val isAdditionalFundAllowed: Boolean,
    val isDirectContributionsAllowed: Boolean,
    val lengthOfPeriod: Int,
    val description: Set<InputTranslation> = emptySet(),
    val priorityPolicies: Set<ProgrammeObjectivePolicy> = emptySet(),
    val strategies: Set<ProgrammeStrategy> = emptySet(),
    val funds: Set<CallFundRate> = emptySet(),
    val stateAidIds: Set<Long> = emptySet(),
) {
    fun is2StepProcedureEnabled() = endDateStep1 != null
}
