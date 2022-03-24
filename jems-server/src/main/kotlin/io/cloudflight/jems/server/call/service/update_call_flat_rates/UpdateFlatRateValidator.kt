package io.cloudflight.jems.server.call.service.update_call_flat_rates

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate

private const val OUT_OF_RANGE_ERR = "call.flatRateSetup.rate.out.of.range"
private const val MAX_FLAT_RATE = 100
private const val MIN_FLAT_RATE = 1

fun validateFlatRates(flatRates: Set<ProjectCallFlatRate>) {
    val groupedByType = flatRates.associateBy { it.type }
    if (groupedByType.keys.size != flatRates.size) {
        throw DuplicateFlatRateTypesDefined()
    }
    val errors = flatRates
        .filter { it.rate > MAX_FLAT_RATE || it.rate < MIN_FLAT_RATE }
        .associateBy({ it.type.name }, { I18nMessage(i18nKey = OUT_OF_RANGE_ERR) })

    if (errors.isNotEmpty())
        throw FlatRateOutOfBounds(errors)
}
