package io.cloudflight.jems.api.project.dto

data class OutputProjectPartnerContact (
    val type: PartnerContactPersonType,
    val title: String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val telephone: String?
)
