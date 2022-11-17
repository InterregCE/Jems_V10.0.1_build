package io.cloudflight.jems.server.controllerInstitution.service.model

import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole

data class InstitutionPartnerDetails(
    val institutionId: Long?,
    val partnerId: Long,
    val partnerName: String,
    val partnerStatus: Boolean,
    val partnerRole: ProjectPartnerRole,
    val partnerSortNumber: Int,
    val partnerNuts3: String?,
    val partnerNuts3Code: String?,
    val country: String?,
    val countryCode: String?,
    val city: String?,
    val postalCode: String?,
    val callId: Long,
    val projectId: Long,
    val projectCustomIdentifier: String,
    val projectAcronym: String,
    val partnerNutsCompatibleInstitutions: MutableSet<IdNamePair>? = mutableSetOf()
)
