package io.cloudflight.jems.api.controllerInstitutions.dto

data class InstitutionPartnerSearchRequestDTO(
    val callId: Long?,
    val projectId: String?,
    val acronym: String?,
    val partnerName: String?,
    val partnerNuts: Set<String>
)
