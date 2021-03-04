package io.cloudflight.jems.server.call.service.update_call_flat_rates

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate

private const val OUT_OF_RANGE_ERR = "call.flatRateSetup.rate.out.of.range"

private val maxFlatRates = mapOf(
    FlatRateType.STAFF_COSTS to 20,
    FlatRateType.OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS to 15,
    FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS to 25,
    FlatRateType.TRAVEL_AND_ACCOMMODATION_ON_STAFF_COSTS to 15,
    FlatRateType.OTHER_COSTS_ON_STAFF_COSTS to 40
)

fun validateFlatRates(flatRates: Set<ProjectCallFlatRate>) {
    val groupedByType = flatRates.associateBy { it.type }
    if (groupedByType.keys.size != flatRates.size) {
        throw DuplicateFlatRateTypesDefined()
    }
    val errors = flatRates
        .filter { it.rate > getMaxRateForType(it.type) || it.rate < 1 }
        .associateBy({ it.type.name }, { I18nMessage(i18nKey = OUT_OF_RANGE_ERR) })

    if (errors.isNotEmpty())
        throw FlatRateOutOfBounds(errors)
}

private fun getMaxRateForType(type: FlatRateType): Int {
    return maxFlatRates.getValue(type)
}
