package io.cloudflight.jems.api.nuts.dto

data class OutputNuts (
    val code: String,
    val title: String,
    val areas: List<OutputNuts> = emptyList()
)
