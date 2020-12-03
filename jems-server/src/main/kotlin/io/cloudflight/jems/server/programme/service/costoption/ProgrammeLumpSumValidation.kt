package io.cloudflight.jems.server.programme.service.costoption

import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import org.springframework.http.HttpStatus
import java.math.BigDecimal

private val MAX_COST = BigDecimal.valueOf(999_999_999_99, 2)

fun validateCreateLumpSum(lumpSum: ProgrammeLumpSum) {
    if (lumpSum.id != null)
        throw I18nValidationException(
            i18nKey = "programme.lumpSum.id.not.allowed",
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
        )
    validateCommonLumpSum(lumpSum = lumpSum)
}

fun validateUpdateLumpSum(lumpSum: ProgrammeLumpSum) {
    if (lumpSum.id == null || lumpSum.id < 1)
        throw I18nValidationException(
            i18nKey = "programme.lumpSum.id.not.valid",
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
        )
    validateCommonLumpSum(lumpSum = lumpSum)
}

private fun validateCommonLumpSum(lumpSum: ProgrammeLumpSum) {
    val errors = mutableMapOf<String, I18nFieldError>()
    if (lumpSum.name.isNullOrBlank())
        errors.put("name", I18nFieldError(i18nKey = "programme.lumpSum.name.should.not.be.empty"))
    else if (lumpSum.name.length > 50)
        errors.put("name", I18nFieldError(i18nKey = "programme.lumpSum.name.too.long"))

    val description = lumpSum.description
    if (description != null && description.length > 500)
        errors.put("description", I18nFieldError(i18nKey = "programme.lumpSum.description.too.long"))

    val cost = lumpSum.cost
    if (cost == null || cost < BigDecimal.ZERO || cost > MAX_COST)
        errors.put("cost", I18nFieldError(i18nKey = "programme.lumpSum.cost.invalid"))

    if (lumpSum.phase == null)
        errors.put("phase", I18nFieldError(i18nKey = "programme.lumpSum.phase.invalid"))

    if (lumpSum.categories.size < 2)
        errors.put("categories", I18nFieldError(i18nKey = "programme.lumpSum.categories.min.2"))

    if (errors.isNotEmpty())
        throw I18nValidationException(
            i18nKey = "programme.lumpSum.invalid",
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nFieldErrors = errors,
        )
}
