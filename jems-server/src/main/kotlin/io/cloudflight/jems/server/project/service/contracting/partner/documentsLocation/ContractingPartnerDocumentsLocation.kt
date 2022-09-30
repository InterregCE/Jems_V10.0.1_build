package io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation

data class ContractingPartnerDocumentsLocation (
    val id: Long = 0,
    val partnerId: Long,
    val title: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val emailAddress: String? = null,
    val telephoneNo: String? = null,
    val institutionName: String? = null,
    val street: String? = null,
    val locationNumber: String? = null,
    val postalCode: String? = null,
    val city: String? = null,
    val homepage: String? = null,
    val country: String? = null,
    val nutsTwoRegion: String? = null,
    val nutsThreeRegion: String? = null,
    val countryCode: String? = null,
    val nutsTwoRegionCode: String? = null,
    val nutsThreeRegionCode: String? = null
)
