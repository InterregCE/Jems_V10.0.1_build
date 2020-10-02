package io.cloudflight.ems.api.call.dto

import java.time.ZonedDateTime

data class OutputCallWithDates (
    val id: Long,
    val name: String,
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime
)
