package io.cloudflight.jems.api.controllerInstitutions.dto

data class InstitutionPartnerSearchRequest(
    val callId: Long?,
    val projectId: String?,
    val acronym: String?,
    val partnerName: String?,
    val partnerNuts: Set<String>,
    var globallyRestrictedNuts: Set<String>? = emptySet(),
)
