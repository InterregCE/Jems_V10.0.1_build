package io.cloudflight.jems.server.project.service.partner.budget.update_budget_options

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import org.springframework.http.HttpStatus

private const val INVALID_ERR_MSG = "project.partner.budget.options.flatRate"

fun validateFlatRates(callFlatRateSetup: Set<ProjectCallFlatRate>, officeAdministrationFlatRate: Int?, staffCostsFlatRate: Int?) {
    val errors: MutableMap<String, I18nFieldError> = mutableMapOf()

    validateFlatRate(
        value = staffCostsFlatRate,
        type = FlatRateType.StaffCost,
        callSetup = callFlatRateSetup,
        errors = errors
    )

    validateFlatRate(
        value = officeAdministrationFlatRate,
        type = FlatRateType.OfficeOnStaff,
        callSetup = callFlatRateSetup,
        errors = errors
    )

    if (errors.isNotEmpty())
        throw I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nKey = INVALID_ERR_MSG,
            i18nFieldErrors = errors
        )
}

private fun validateFlatRate(value: Int?, type: FlatRateType, callSetup: Set<ProjectCallFlatRate>, errors: MutableMap<String, I18nFieldError>) {
    if (value != null) {
        val restriction = callSetup.find { it.type == type }
        if (restriction == null)
            errors[type.name] = I18nFieldError(i18nKey = "$INVALID_ERR_MSG.type.not.allowed")
        else if (value > restriction.rate)
            errors[type.name] = I18nFieldError(i18nKey = "$INVALID_ERR_MSG.exceeded")
        else if (!restriction.isAdjustable && restriction.rate != value)
            errors[type.name] = I18nFieldError(i18nKey = "$INVALID_ERR_MSG.not.adjustable")
    }
}
