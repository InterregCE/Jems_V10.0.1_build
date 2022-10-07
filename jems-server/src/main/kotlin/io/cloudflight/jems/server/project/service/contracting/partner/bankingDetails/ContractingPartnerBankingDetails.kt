package io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails

data class ContractingPartnerBankingDetails(
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
) {
    fun getDiff(old: ContractingPartnerBankingDetails? = null): Map<String, Pair<Any?, Any?>> {
        val changes = mutableMapOf<String, Pair<Any?, Any?>>()

        if (old == null || accountHolder != old.accountHolder)
            changes["accountHolder"] = Pair(old?.accountHolder, accountHolder)
        if (old == null || accountNumber != old.accountNumber)
            changes["accountNumber"] = Pair(old?.accountNumber, accountNumber)
        if (old == null || accountIBAN != old.accountIBAN)
            changes["accountIBAN"] = Pair(old?.accountIBAN, accountIBAN)
        if (old == null || accountSwiftBICCode != old.accountSwiftBICCode)
            changes["accountSwiftBICCode"] = Pair(old?.accountSwiftBICCode, accountSwiftBICCode)

        return changes
    }
}