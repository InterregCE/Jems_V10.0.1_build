package io.cloudflight.jems.api.controllerInstitutions.dto

data class InstitutionPartnerSearchRequestDTO(
    val callId: Long? = null,
    val projectId: String? = null,
    val acronym: String? = null,
    val partnerName: String? = null,
    val partnerNuts: Set<String> = emptySet()
)
