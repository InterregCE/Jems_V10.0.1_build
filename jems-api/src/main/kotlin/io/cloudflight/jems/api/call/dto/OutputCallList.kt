package io.cloudflight.jems.api.call.dto

import java.time.ZonedDateTime

data class OutputCallList (
    val id: Long?,
    val name: String,
    val status: CallStatus,
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime
)
