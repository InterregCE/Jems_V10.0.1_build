package io.cloudflight.jems.server.call.service.flatrate.update_flat_rate_setup

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import org.springframework.http.HttpStatus

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
        invalid("call.flatRateSetup.duplicates")
    }
    val errors = flatRates
        .filter { it.rate > getMaxRateForType(it.type) || it.rate < 1 }
        .associateBy({ it.type.name }, { I18nFieldError(i18nKey = OUT_OF_RANGE_ERR) })

    if (errors.isNotEmpty()) {
        invalid(OUT_OF_RANGE_ERR, errors)
    }
}

private fun getMaxRateForType(type: FlatRateType): Int {
    return maxFlatRates.getValue(type)
}

private fun invalid(message: String, fieldErrors: Map<String, I18nFieldError> = emptyMap()) {
    throw I18nValidationException(
        httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
        i18nKey = message,
        i18nFieldErrors = fieldErrors
    )
}
