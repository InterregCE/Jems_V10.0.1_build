package io.cloudflight.jems.api.call.dto

import java.time.ZonedDateTime

data class CallDTO (
    val id: Long? = null,
    val name: String,
    val status: CallStatus,
    val startDateTime: ZonedDateTime,
    val endDateTime: ZonedDateTime
)
