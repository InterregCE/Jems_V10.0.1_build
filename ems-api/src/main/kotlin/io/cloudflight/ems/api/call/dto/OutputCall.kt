package io.cloudflight.ems.api.call.dto

import java.time.ZonedDateTime

data class OutputCall (
    val id: Long?,
    val name: String,
    val status: CallStatus,
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime,
    val description: String? = null
)
