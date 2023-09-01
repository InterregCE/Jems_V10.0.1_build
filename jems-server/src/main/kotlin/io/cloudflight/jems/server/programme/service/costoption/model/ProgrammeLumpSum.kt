package io.cloudflight.jems.server.programme.service.costoption.model

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProgrammeLumpSum(
    val id: Long = 0,
    val name: Set<InputTranslation> = emptySet(),
    val description: Set<InputTranslation> = emptySet(),
    val cost: BigDecimal? = null,
    val splittingAllowed: Boolean,
    val fastTrack: Boolean,
    val phase: ProgrammeLumpSumPhase? = null,
    val categories: Set<BudgetCategory> = emptySet(),
    val paymentClaim: PaymentClaim
) {

    fun getDiff(old: ProgrammeLumpSum? = null): Map<String, Pair<Any?, Any?>> {
        val changes = mutableMapOf<String, Pair<Any?, Any?>>()

        if (name != (old?.name ?: emptySet<InputTranslation>()))
            changes["name"] = Pair(old?.name, name)

        if (description != (old?.description ?: emptySet<InputTranslation>()))
            changes["description"] = Pair(old?.description, description)

        if ((cost?: BigDecimal.ZERO).compareTo(old?.cost ?: BigDecimal.ZERO) != 0)
            changes["costPerUnit"] = Pair(old?.cost, cost)

        if (splittingAllowed != old?.splittingAllowed)
            changes["splittingAllowed"] = Pair(old?.splittingAllowed, splittingAllowed)

        if (fastTrack != old?.fastTrack)
            changes["fastTrack"] = Pair(old?.fastTrack, fastTrack)

        if (phase != old?.phase)
            changes["phase"] = Pair(old?.phase, phase)

        if (categories != (old?.categories ?: emptySet<BudgetCategory>()))
            changes["categories"] = Pair(old?.categories ?: "", categories)

        if (paymentClaim != old?.paymentClaim)
            changes["paymentClaim"] = Pair(old?.paymentClaim, paymentClaim)

        return changes
    }
}
