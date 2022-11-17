package io.cloudflight.jems.server.programme.service.costoption

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import org.springframework.http.HttpStatus
import java.math.BigDecimal

private val MAX_COST = BigDecimal.valueOf(999_999_999_99, 2)
const val MAX_ALLOWED_UNIT_COSTS = 200

fun validateCreateUnitCost(unitCostToValidate: ProgrammeUnitCost, currentCount: Long, maxAllowed: Int) {
    if (unitCostToValidate.id != 0L)
        throw I18nValidationException(i18nKey = "programme.unitCost.id.not.allowed")
    if (currentCount >= maxAllowed)
        throw I18nValidationException(i18nKey = "programme.unitCost.max.allowed.reached")
    validateCommonUnitCost(unitCost = unitCostToValidate)
}

fun validateUpdateUnitCost(unitCost: ProgrammeUnitCost) {
    if (unitCost.id == 0L)
        throw I18nValidationException(
            i18nKey = "programme.unitCost.id.not.valid",
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
        )
    validateCommonUnitCost(unitCost = unitCost)
}

fun ProgrammeUnitCost.getStaticValidationResults(validator: GeneralValidatorService) = listOf(
    validator.maxLength(name, 50, "name"),
    validator.maxLength(description, 255, "description"),
    validator.maxLength(type, 25, "type"),
    validator.maxLength(justification, 5000, "justification"),
)

private fun validateCommonUnitCost(unitCost: ProgrammeUnitCost) {
    val errors = mutableMapOf<String, I18nMessage>()

    validateCostPerUnit(unitCost.costPerUnit, errors)
    validateCostPerUnitForeignCurrency(unitCost.costPerUnitForeignCurrency, errors)
    validateOneCostCategory(unitCost = unitCost, errors = errors)

    if (errors.isNotEmpty())
        throw AppInputValidationException(formErrors = errors)
}

private fun validateCostPerUnit(costPerUnit: BigDecimal?, errors: MutableMap<String, I18nMessage>) {
    if (costPerUnit == null || costPerUnit <= BigDecimal.ZERO || costPerUnit > MAX_COST || costPerUnit.scale() > 2)
        errors["costPerUnit"] = I18nMessage(
            i18nKey = "programme.unitCost.costPerUnit.invalid",
            mapOf("costPerUnit" to costPerUnit.toString())
        )
}

private fun validateOneCostCategory(unitCost: ProgrammeUnitCost, errors: MutableMap<String, I18nMessage>) {
    if (unitCost.isOneCostCategory) {
        if (unitCost.categories.size != 1)

            errors["categories"] = I18nMessage(i18nKey = "programme.unitCost.categories.exactly.1")

        else {
            if (unitCost.categories.contains(BudgetCategory.OfficeAndAdministrationCosts))
                errors["categories"] = I18nMessage(i18nKey = "programme.unitCost.categories.restricted")
        }
    } else {
        if (unitCost.categories.size < 2)
            errors["categories"] = I18nMessage(i18nKey = "programme.unitCost.categories.min.2")
    }
}

private fun validateCostPerUnitForeignCurrency(
    costPerUnitForeignCurrency: BigDecimal?,
    errors: MutableMap<String, I18nMessage>
) {
    if (costPerUnitForeignCurrency != null && isInvalidCostValue(costPerUnitForeignCurrency)) {
        errors["costPerUnitForeignCurrency"] = I18nMessage(
            i18nKey = "programme.unitCost.costPerUnit.invalid",
            mapOf("costPerUnitForeignCurrency" to costPerUnitForeignCurrency.toString())
        )
    }
}

private fun isInvalidCostValue(value: BigDecimal): Boolean =
    value <= BigDecimal.ZERO || value > MAX_COST || value.scale() > 2
