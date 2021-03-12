package io.cloudflight.jems.server.call.service.model

import io.cloudflight.jems.api.call.dto.CallStatus
import java.time.ZonedDateTime

data class CallSummary (
    val id: Long,
    val name: String,
    val status: CallStatus,
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime
)
