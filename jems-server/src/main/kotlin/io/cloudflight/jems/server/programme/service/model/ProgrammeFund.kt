package io.cloudflight.jems.server.programme.service.model

data class ProgrammeFund(
    val id: Long = 0,
    val abbreviation: String? = null,
    val description: String? = null,
    val selected: Boolean = false
)
