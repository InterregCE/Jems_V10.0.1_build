package io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner

import java.time.LocalDate

data class ContractingPartnerBeneficialOwner(
    val id: Long,
    val partnerId: Long,
    val firstName: String,
    val lastName: String,
    val birth: LocalDate?,
    val vatNumber: String,
) {
    fun getDiff(old: ContractingPartnerBeneficialOwner? = null): Map<String, Pair<Any?, Any?>> {
        val changes = mutableMapOf<String, Pair<Any?, Any?>>()

        if (old == null || firstName != old.firstName)
            changes["firstName"] = Pair(old?.firstName, firstName)
        if (old == null || lastName != old.lastName)
            changes["lastName"] = Pair(old?.lastName, lastName)
        if (old == null || birth != old.birth)
            changes["birth"] = Pair(old?.birth, birth)
        if (old == null || vatNumber != old.vatNumber)
            changes["vatNumber"] = Pair(old?.vatNumber, vatNumber)

        return changes
    }
}
