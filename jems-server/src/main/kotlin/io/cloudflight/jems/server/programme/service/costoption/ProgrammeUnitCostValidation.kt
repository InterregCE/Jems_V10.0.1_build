package io.cloudflight.jems.server.programme.service.costoption

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import org.springframework.http.HttpStatus
import java.math.BigDecimal

private val MAX_COST = BigDecimal.valueOf(999_999_999_99, 2)
private const val MAX_ALLOWED_UNIT_COSTS = 25

fun validateCreateUnitCost(unitCostToValidate: ProgrammeUnitCost, currentCount: Long) {
    if (unitCostToValidate.id != null)
        throw I18nValidationException(i18nKey = "programme.unitCost.id.not.allowed")
    if (currentCount >= MAX_ALLOWED_UNIT_COSTS)
        throw I18nValidationException(i18nKey = "programme.unitCost.max.allowed.reached")
    validateCommonUnitCost(unitCost = unitCostToValidate)
}

fun validateUpdateUnitCost(unitCost: ProgrammeUnitCost) {
    if (unitCost.id == null || unitCost.id < 1)
        throw I18nValidationException(
            i18nKey = "programme.unitCost.id.not.valid",
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
        )
    validateCommonUnitCost(unitCost = unitCost)
}

private fun validateCommonUnitCost(unitCost: ProgrammeUnitCost) {
    val errors = mutableMapOf<String, I18nFieldError>()

    if (unitCost.name.any { it.translation != null && it.translation!!.length > 50 })
        errors.put("name", I18nFieldError(i18nKey = "programme.unitCost.name.too.long"))

    if (unitCost.description.any { it.translation != null && it.translation!!.length > 500 })
        errors.put("description", I18nFieldError(i18nKey = "programme.unitCost.description.too.long"))

    if (unitCost.type.any { it.translation != null && it.translation!!.length > 25 })
        errors.put("type", I18nFieldError(i18nKey = "programme.unitCost.type.too.long"))

    val costPerUnit = unitCost.costPerUnit
    if (costPerUnit == null || costPerUnit < BigDecimal.ZERO || costPerUnit > MAX_COST || costPerUnit.scale() > 2)
        errors.put("costPerUnit", I18nFieldError(i18nKey = "programme.unitCost.costPerUnit.invalid"))

    validateOneCostCategory(unitCost = unitCost, errors = errors)

    if (errors.isNotEmpty())
        throw I18nValidationException(
            i18nKey = "programme.unitCost.invalid",
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nFieldErrors = errors,
        )
}

private fun validateOneCostCategory(unitCost: ProgrammeUnitCost, errors: MutableMap<String, I18nFieldError>) {
    if (unitCost.isOneCostCategory == true) {
        if (unitCost.categories.size != 1)
            errors.put("categories", I18nFieldError(i18nKey = "programme.unitCost.categories.exactly.1"))
        else {
            if (unitCost.categories.contains(BudgetCategory.OfficeAndAdministrationCosts))
                errors.put("categories", I18nFieldError(i18nKey = "programme.unitCost.categories.restricted"))
        }
    } else {
        if (unitCost.categories.size < 2)
            errors.put("categories", I18nFieldError(i18nKey = "programme.unitCost.categories.min.2"))
    }
}
