package io.cloudflight.jems.server.project.service.partner.budget.update_budget_options

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions

const val INVALID_FLAT_RATE_COMBINATION_ERROR_KEY =
    "$UPDATE_BUDGET_OPTIONS_ERROR_KEY_PREFIX.flat.rate.combination.is.not.valid"

fun validateFlatRatesCombinations(options: ProjectPartnerBudgetOptions) {
    val errors: MutableMap<String, I18nMessage> = mutableMapOf()

    if (options.officeAndAdministrationOnStaffCostsFlatRate != null && options.officeAndAdministrationOnDirectCostsFlatRate != null)
        errors.apply {
            this[FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS.key] =
                I18nMessage(i18nKey = INVALID_FLAT_RATE_COMBINATION_ERROR_KEY)
            this[FlatRateType.OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS.key] =
                I18nMessage(i18nKey = INVALID_FLAT_RATE_COMBINATION_ERROR_KEY)
        }

    val areOtherCostsApplicable =
        options.officeAndAdministrationOnStaffCostsFlatRate != null || options.officeAndAdministrationOnDirectCostsFlatRate != null || options.travelAndAccommodationOnStaffCostsFlatRate != null || options.staffCostsFlatRate != null
    if (options.otherCostsOnStaffCostsFlatRate != null && areOtherCostsApplicable)
        errors.apply {
            this[FlatRateType.OTHER_COSTS_ON_STAFF_COSTS.key] =
                I18nMessage(i18nKey = INVALID_FLAT_RATE_COMBINATION_ERROR_KEY)

        }

    if (errors.isNotEmpty())
        throw InvalidFlatRateCombinationException(formErrors = errors)
}

fun validateFlatRates(callFlatRateSetup: Set<ProjectCallFlatRate>, options: ProjectPartnerBudgetOptions) {
    val errors: MutableMap<String, I18nMessage> = mutableMapOf()

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
        throw InvalidFlatRateException(formErrors = errors)
}

private fun validateFlatRate(
    value: Int?,
    type: FlatRateType,
    callSetup: Set<ProjectCallFlatRate>,
    errors: MutableMap<String, I18nMessage>
) {
    if (value != null) {
        val restriction = callSetup.find { it.type == type }
        if (restriction == null)
            errors[type.key] =
                I18nMessage(i18nKey = "$UPDATE_BUDGET_OPTIONS_ERROR_KEY_PREFIX.flat.rate.type.not.allowed.error")
        else if (value > restriction.rate)
            errors[type.key] =
                I18nMessage(
                    i18nKey = "$UPDATE_BUDGET_OPTIONS_ERROR_KEY_PREFIX.flat.rate.range.error",
                    i18nArguments = hashMapOf(Pair("maxValue", restriction.rate.toString()))
                )
        else if (!restriction.isAdjustable && restriction.rate != value)
            errors[type.key] =
                I18nMessage(i18nKey = "$UPDATE_BUDGET_OPTIONS_ERROR_KEY_PREFIX.flat.rate.not.adjustable.error")
    }
}
