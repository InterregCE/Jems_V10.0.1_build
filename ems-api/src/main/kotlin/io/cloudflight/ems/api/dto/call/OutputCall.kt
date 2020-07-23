package io.cloudflight.ems.api.dto.call

import java.time.ZonedDateTime

data class OutputCall (
    val id: Long?,
    val creator: String,
    val name: String,
    val status: CallStatus,
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime
)
