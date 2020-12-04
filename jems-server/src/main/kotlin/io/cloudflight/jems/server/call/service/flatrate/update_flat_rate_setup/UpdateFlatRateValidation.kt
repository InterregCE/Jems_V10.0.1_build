package io.cloudflight.jems.server.call.service.flatrate.update_flat_rate_setup

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import org.springframework.http.HttpStatus

private const val OUT_OF_RANGE_ERR = "call.flatRateSetup.rate.out.of.range"

private val maxFlatRates = mapOf(
    FlatRateType.StaffCost to 20,
    FlatRateType.OfficeOnStaff to 15,
    FlatRateType.OfficeOnOther to 25,
    FlatRateType.TravelOnStaff to 15,
    FlatRateType.OtherOnStaff to 40
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
