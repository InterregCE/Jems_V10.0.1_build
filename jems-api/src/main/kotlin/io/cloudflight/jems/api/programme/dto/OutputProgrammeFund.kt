package io.cloudflight.jems.api.programme.dto

data class OutputProgrammeFund (
    val id: Long,
    val abbreviation: String? = null,
    val description: String? = null,
    val selected: Boolean
)
