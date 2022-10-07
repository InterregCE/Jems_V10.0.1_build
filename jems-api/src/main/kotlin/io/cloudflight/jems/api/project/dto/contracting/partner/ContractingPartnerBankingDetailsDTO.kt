package io.cloudflight.jems.api.project.dto.contracting.partner

data class ContractingPartnerBankingDetailsDTO (
    val partnerId: Long,
    val accountHolder: String?,
    val accountNumber: String?,
    val accountIBAN: String?,
    val accountSwiftBICCode: String?,
    val bankName: String?,
    val streetName: String?,
    val streetNumber: String?,
    val postalCode: String?,
    val country: String?,
    val nutsTwoRegion: String?,
    val nutsThreeRegion: String?
)