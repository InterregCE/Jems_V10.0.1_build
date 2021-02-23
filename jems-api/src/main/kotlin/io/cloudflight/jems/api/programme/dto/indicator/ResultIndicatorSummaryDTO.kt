package io.cloudflight.jems.api.programme.dto.indicator

data class ResultIndicatorSummaryDTO(
    val id: Long?,
    val identifier: String,
    val code: String?,
    val name: String,
    val programmePriorityCode: String?,
    val measurementUnit: String?
)
