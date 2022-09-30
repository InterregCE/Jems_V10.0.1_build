package io.cloudflight.jems.api.project.dto.contracting.partner

data class ContractingPartnerDocumentsLocationDTO (
    val id: Long = 0,
    val partnerId: Long,
    val title: String?,
    val firstName: String?,
    val lastName: String?,
    val emailAddress: String?,
    val telephoneNo: String?,
    val institutionName: String?,
    val street: String?,
    val locationNumber: String?,
    val postalCode: String?,
    val city: String?,
    val homepage: String?,
    val country: String?,
    val nutsTwoRegion: String?,
    val nutsThreeRegion: String?,
    val countryCode: String?,
    val nutsTwoRegionCode: String?,
    val nutsThreeRegionCode: String?
)
