package io.cloudflight.ems.service.call.mapper

import io.cloudflight.ems.api.dto.call.CallStatus
import io.cloudflight.ems.api.dto.call.InputCallCreate
import io.cloudflight.ems.api.dto.call.OutputCall
import io.cloudflight.ems.entity.Call
import io.cloudflight.ems.entity.User

fun InputCallCreate.toEntity(creator: User) = Call(
    id = null,
    name = this.name,
    creator = creator,
    status = CallStatus.DRAFT,
    startDate = this.startDate,
    endDate = this.endDate,
    description = this.description
)

fun Call.toOutputCall() = OutputCall(
    id = this.id,
    name = this.name,
    creator = this.creator.email,
    status = this.status,
    startDate = this.startDate,
    endDate = this.endDate
)
