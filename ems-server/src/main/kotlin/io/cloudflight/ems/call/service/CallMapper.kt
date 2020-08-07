package io.cloudflight.ems.call.service

import io.cloudflight.ems.api.call.dto.CallStatus
import io.cloudflight.ems.api.call.dto.InputCallCreate
import io.cloudflight.ems.api.call.dto.OutputCall
import io.cloudflight.ems.api.call.dto.OutputCallSimple
import io.cloudflight.ems.call.entity.Call
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.programme.entity.ProgrammePriorityPolicy
import io.cloudflight.ems.programme.service.toOutputProgrammePriorityPolicy

fun InputCallCreate.toEntity(creator: User, priorityPolicies: Set<ProgrammePriorityPolicy>) = Call(
    id = null,
    creator = creator,
    name = name!!,
    priorityPolicies = priorityPolicies,
    status = CallStatus.DRAFT,
    startDate = startDate!!,
    endDate = endDate!!.withSecond(0).withNano(0).plusMinutes(1).minusNanos(1),
    description = description
)

fun Call.toOutputCall() = OutputCall(
    id = id,
    name = name,
    priorityPolicies = priorityPolicies.map { it.toOutputProgrammePriorityPolicy() },
    status = status,
    startDate = startDate,
    endDate = endDate,
    description = description
)

fun Call.toOutputCallSimple() = OutputCallSimple(
    id = id!!,
    name = name
)
