package io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation

data class ContractingPartnerDocumentsLocation(
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
) {
    fun getDiff(old: ContractingPartnerDocumentsLocation? = null): Map<String, Pair<Any?, Any?>> {
        val changes = mutableMapOf<String, Pair<Any?, Any?>>()

        extractedBaseInfo(old, changes)
        extractedLocationInfo(old, changes)
        extractedAdditionalLocationInfo(old, changes)

        return changes
    }

    private fun extractedBaseInfo(
        old: ContractingPartnerDocumentsLocation?,
        changes: MutableMap<String, Pair<Any?, Any?>>
    ) {
        if (old == null || title != old.title)
            changes["title"] = Pair(old?.title, title)
        if (old == null || firstName != old.firstName)
            changes["firstName"] = Pair(old?.firstName, firstName)
        if (old == null || lastName != old.lastName)
            changes["lastName"] = Pair(old?.lastName, lastName)
        if (old == null || emailAddress != old.emailAddress)
            changes["emailAddress"] = Pair(old?.emailAddress, emailAddress)
        if (old == null || telephoneNo != old.telephoneNo)
            changes["telephoneNo"] = Pair(old?.telephoneNo, telephoneNo)
    }

    private fun extractedLocationInfo(
        old: ContractingPartnerDocumentsLocation?,
        changes: MutableMap<String, Pair<Any?, Any?>>
    ) {
        if (old == null || institutionName != old.institutionName)
            changes["institutionName"] = Pair(old?.institutionName, institutionName)
        if (old == null || street != old.street)
            changes["street"] = Pair(old?.street, street)
        if (old == null || locationNumber != old.locationNumber)
            changes["locationNumber"] = Pair(old?.locationNumber, locationNumber)
        if (old == null || postalCode != old.postalCode)
            changes["postalCode"] = Pair(old?.postalCode, postalCode)
        if (old == null || city != old.city)
            changes["city"] = Pair(old?.city, city)
        if (old == null || homepage != old.homepage)
            changes["homepage"] = Pair(old?.homepage, homepage)
    }

    private fun extractedAdditionalLocationInfo(
        old: ContractingPartnerDocumentsLocation?,
        changes: MutableMap<String, Pair<Any?, Any?>>
    ) {
        if (old == null || country != old.country)
            changes["country"] = Pair(old?.country, country)
        if (old == null || nutsTwoRegion != old.nutsTwoRegion)
            changes["nutsTwoRegion"] = Pair(old?.nutsTwoRegion, nutsTwoRegion)
        if (old == null || nutsThreeRegion != old.nutsThreeRegion)
            changes["nutsThreeRegion"] = Pair(old?.nutsThreeRegion, nutsThreeRegion)
    }
}