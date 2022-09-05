package io.cloudflight.jems.server.controllerInstitution.service.model

interface InstitutionPartnerDetailsRow {
    val institutionId: Long?
    val partnerId: Long
    val partnerName: String
    val partnerStatus: Boolean
    val partnerSortNumber: Int
    val partnerRole: String
    val partnerNuts3: String?
    val partnerNuts3Code: String?
    val country: String?
    val countryCode: String?
    val city: String?
    val postalCode: String?
    val callId: Long
    val projectId: Long
    val projectCustomIdentifier: String
    val projectAcronym: String
}
