package io.cloudflight.ems.api.programme.dto

data class OutputProgrammeFund (
    val id: Long,
    val abbreviation: String? = null,
    val description: String? = null,
    val selected: Boolean
)
