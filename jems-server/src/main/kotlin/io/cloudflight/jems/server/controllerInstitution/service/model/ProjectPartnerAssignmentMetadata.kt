package io.cloudflight.jems.server.controllerInstitution.service.model

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole

data class ProjectPartnerAssignmentMetadata(
    val partnerId: Long,

    val partnerNumber: Int,
    val partnerAbbreviation: String,
    val partnerRole: ProjectPartnerRole,
    val partnerActive: Boolean,
    val addressNuts3: String?,
    val addressNuts3Code: String?,
    val addressCountry: String?,
    val addressCountryCode: String?,
    val addressCity: String?,
    val addressPostalCode: String?,

    val projectIdentifier: String,
    val projectAcronym: String
)
