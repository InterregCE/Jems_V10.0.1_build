package io.cloudflight.ems.call.service

import io.cloudflight.ems.api.call.dto.CallStatus
import io.cloudflight.ems.api.call.dto.InputCallCreate
import io.cloudflight.ems.api.call.dto.OutputCall
import io.cloudflight.ems.api.call.dto.OutputCallList
import io.cloudflight.ems.api.call.dto.OutputCallWithDates
import io.cloudflight.ems.call.entity.Call
import io.cloudflight.ems.programme.entity.ProgrammeFund
import io.cloudflight.ems.user.entity.User
import io.cloudflight.ems.programme.entity.ProgrammePriorityPolicy
import io.cloudflight.ems.programme.service.toOutputProgrammeFund
import io.cloudflight.ems.programme.service.toOutputProgrammePriorityPolicy
import io.cloudflight.ems.strategy.entity.Strategy

/**
 * Map InputCallCreate to entity Call.
 * The specified minute is included in the end time of the call by adding all of its seconds.
 */
fun InputCallCreate.toEntity(
    creator: User,
    priorityPolicies: Set<ProgrammePriorityPolicy>,
    strategies: Set<Strategy>,
    funds: Set<ProgrammeFund>
) = Call(
    id = null,
    creator = creator,
    name = name!!,
    priorityPolicies = priorityPolicies,
    strategies = strategies,
    funds = funds,
    status = CallStatus.DRAFT,
    startDate = startDate!!.withSecond(0).withNano(0),
    endDate = endDate!!.withSecond(0).withNano(0).plusMinutes(1).minusNanos(1),
    description = description,
    lengthOfPeriod = lengthOfPeriod
)

fun Call.toOutputCall() = OutputCall(
    id = id,
    name = name,
    priorityPolicies = priorityPolicies.map { it.toOutputProgrammePriorityPolicy() },
    strategies = strategies.map { it.strategy },
    funds = funds.map { it.toOutputProgrammeFund() },
    status = status,
    startDate = startDate,
    endDate = endDate,
    description = description,
    lengthOfPeriod = lengthOfPeriod
)

fun Call.toOutputCallList() = OutputCallList(
    id = id,
    name = name,
    status = status,
    startDate = startDate,
    endDate = endDate
)

fun Call.toOutputCallWithDates() = OutputCallWithDates(
    id = id!!,
    name = name,
    startDate = startDate,
    endDate = endDate
)
