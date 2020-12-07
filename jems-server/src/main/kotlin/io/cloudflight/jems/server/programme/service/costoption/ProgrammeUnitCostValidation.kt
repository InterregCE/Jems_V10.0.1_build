package io.cloudflight.jems.server.programme.service.costoption

import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import org.springframework.http.HttpStatus
import java.math.BigDecimal

private val MAX_COST = BigDecimal.valueOf(999_999_999_99, 2)

fun validateCreateUnitCost(unitCost: ProgrammeUnitCost) {
    if (unitCost.id != null)
        throw I18nValidationException(
            i18nKey = "programme.unitCost.id.not.allowed",
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
        )
    validateCommonUnitCost(unitCost = unitCost)
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
    if (unitCost.name.isNullOrBlank())
        errors.put("name", I18nFieldError(i18nKey = "programme.unitCost.name.should.not.be.empty"))
    else if (unitCost.name.length > 50)
        errors.put("name", I18nFieldError(i18nKey = "programme.unitCost.name.too.long"))

    val description = unitCost.description
    if (description != null && description.length > 500)
        errors.put("description", I18nFieldError(i18nKey = "programme.unitCost.description.too.long"))

    val type = unitCost.type
    if (type == null)
        errors.put("type", I18nFieldError(i18nKey = "programme.unitCost.type.should.not.be.empty"))
    else if (type.length > 25)
        errors.put("type", I18nFieldError(i18nKey = "programme.unitCost.type.too.long"))

    val costPerUnit = unitCost.costPerUnit
    if (costPerUnit == null || costPerUnit < BigDecimal.ZERO || costPerUnit > MAX_COST || costPerUnit.scale() > 2)
        errors.put("costPerUnit", I18nFieldError(i18nKey = "programme.unitCost.costPerUnit.invalid"))

    if (unitCost.categories.size < 2)
        errors.put("categories", I18nFieldError(i18nKey = "programme.unitCost.categories.min.2"))

    if (errors.isNotEmpty())
        throw I18nValidationException(
            i18nKey = "programme.unitCost.invalid",
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nFieldErrors = errors,
        )
}
