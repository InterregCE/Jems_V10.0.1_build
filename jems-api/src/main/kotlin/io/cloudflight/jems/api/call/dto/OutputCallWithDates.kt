package io.cloudflight.jems.api.call.dto

import io.cloudflight.jems.api.call.dto.flatrate.InputCallFlatRateSetup
import java.time.ZonedDateTime

data class OutputCallWithDates (
    val id: Long,
    val name: String,
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime,
    val lengthOfPeriod: Int,
    val flatRates: List<InputCallFlatRateSetup> = emptyList()
)
