package io.cloudflight.ems.service.call.mapper

import io.cloudflight.ems.api.call.dto.CallStatus
import io.cloudflight.ems.api.call.dto.InputCallCreate
import io.cloudflight.ems.api.call.dto.OutputCall
import io.cloudflight.ems.api.call.dto.OutputCallSimple
import io.cloudflight.ems.entity.Call
import io.cloudflight.ems.entity.User

fun InputCallCreate.toEntity(creator: User) = Call(
    id = null,
    name = name!!,
    creator = creator,
    status = CallStatus.DRAFT,
    startDate = startDate!!,
    endDate = endDate!!,
    description = description
)

fun Call.toOutputCall() = OutputCall(
    id = id,
    name = name,
    status = status,
    startDate = startDate,
    endDate = endDate,
    description = description
)

fun Call.toOutputCallSimple() = OutputCallSimple(
    id = id!!,
    name = name
)
