package io.cloudflight.ems.api.project.dto

data class OutputProjectPartnerDetail (
    val id: Long?,
    val name: String,
    val role: ProjectPartnerRole,
    val sortNumber: Int? = null,
    val partnerContactPersons: Set<OutputProjectPartnerContact>? = null,
    val partnerContribution: OutputProjectPartnerContribution? = null
)
