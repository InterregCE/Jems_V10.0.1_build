package io.cloudflight.jems.server.project.service.partner.budget.update_budget_options

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import org.springframework.http.HttpStatus

private const val INVALID_ERR_MSG = "project.partner.budget.options.flatRate"

fun validateFlatRatesCombinations(options: ProjectPartnerBudgetOptions) {
    val errors: MutableMap<String, I18nFieldError> = mutableMapOf()

    if (options.officeAndAdministrationOnStaffCostsFlatRate != null && options.officeAndAdministrationOnDirectCostsFlatRate != null)
        throw I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nKey = INVALID_ERR_MSG,
            i18nFieldErrors = errors.apply {
                this[FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS.name] =
                    I18nFieldError(i18nKey = "$INVALID_ERR_MSG.combination.is.not.valid")
                this[FlatRateType.OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS.name] =
                    I18nFieldError(i18nKey = "$INVALID_ERR_MSG.combination.is.not.valid")
            }
        )

    val areOtherCostsApplicable =
        options.officeAndAdministrationOnStaffCostsFlatRate != null || options.officeAndAdministrationOnDirectCostsFlatRate != null || options.travelAndAccommodationOnStaffCostsFlatRate != null || options.staffCostsFlatRate != null
    if (options.otherCostsOnStaffCostsFlatRate != null && areOtherCostsApplicable)
        throw I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nKey = INVALID_ERR_MSG,
            i18nFieldErrors = errors.apply {
                this[FlatRateType.OTHER_COSTS_ON_STAFF_COSTS.name] =
                    I18nFieldError(i18nKey = "$INVALID_ERR_MSG.combination.is.not.valid")
            }
        )

    if (errors.isNotEmpty())
        throw I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nKey = INVALID_ERR_MSG,
            i18nFieldErrors = errors
        )
}

fun validateFlatRates(callFlatRateSetup: Set<ProjectCallFlatRate>, options: ProjectPartnerBudgetOptions) {
    val errors: MutableMap<String, I18nFieldError> = mutableMapOf()

    validateFlatRate(
        value = options.otherCostsOnStaffCostsFlatRate,
        type = FlatRateType.OTHER_COSTS_ON_STAFF_COSTS,
        callSetup = callFlatRateSetup,
        errors = errors
    )

    validateFlatRate(
        value = options.staffCostsFlatRate,
        type = FlatRateType.STAFF_COSTS,
        callSetup = callFlatRateSetup,
        errors = errors
    )

    validateFlatRate(
        value = options.officeAndAdministrationOnStaffCostsFlatRate,
        type = FlatRateType.OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS,
        callSetup = callFlatRateSetup,
        errors = errors
    )

    validateFlatRate(
        value = options.officeAndAdministrationOnDirectCostsFlatRate,
        type = FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS,
        callSetup = callFlatRateSetup,
        errors = errors
    )

    validateFlatRate(
        value = options.travelAndAccommodationOnStaffCostsFlatRate,
        type = FlatRateType.TRAVEL_AND_ACCOMMODATION_ON_STAFF_COSTS,
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

private fun validateFlatRate(
    value: Int?,
    type: FlatRateType,
    callSetup: Set<ProjectCallFlatRate>,
    errors: MutableMap<String, I18nFieldError>
) {
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
