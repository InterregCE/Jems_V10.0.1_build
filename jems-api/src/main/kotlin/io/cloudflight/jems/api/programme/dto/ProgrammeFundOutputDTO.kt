package io.cloudflight.jems.api.programme.dto

data class ProgrammeFundOutputDTO (
    val selected: Boolean,
    val id: Long,
    val abbreviation: String? = null,
    val description: String? = null
)
