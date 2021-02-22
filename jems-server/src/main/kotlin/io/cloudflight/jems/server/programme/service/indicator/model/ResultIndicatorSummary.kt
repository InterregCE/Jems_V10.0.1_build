package io.cloudflight.jems.server.programme.service.indicator.model

data class ResultIndicatorSummary(
    val id: Long?,
    val identifier: String,
    val code: String?,
    val name: String,
    val programmePriorityCode: String?,
    val measurementUnit: String?
)
