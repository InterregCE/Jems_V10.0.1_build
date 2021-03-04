package io.cloudflight.jems.server.programme.service.costoption

import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import org.springframework.http.HttpStatus
import java.math.BigDecimal

private val MAX_COST = BigDecimal.valueOf(999_999_999_99, 2)
private const val MAX_ALLOWED_LUMP_SUMS = 25

fun validateCreateLumpSum(lumpSumToValidate: ProgrammeLumpSum, currentCount: Long) {
    if (lumpSumToValidate.id != 0L)
        throw I18nValidationException(i18nKey = "programme.lumpSum.id.not.allowed")
    if (currentCount >= MAX_ALLOWED_LUMP_SUMS)
        throw I18nValidationException(i18nKey = "programme.lumpSum.max.allowed.reached")
    validateCommonLumpSum(lumpSum = lumpSumToValidate)
}

fun validateUpdateLumpSum(lumpSum: ProgrammeLumpSum) {
    if (lumpSum.id < 1)
        throw I18nValidationException(
            i18nKey = "programme.lumpSum.id.not.valid",
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
        )
    validateCommonLumpSum(lumpSum = lumpSum)
}

private fun validateCommonLumpSum(lumpSum: ProgrammeLumpSum) {
    val errors = mutableMapOf<String, I18nFieldError>()

    if (lumpSum.name.any { it.translation != null && it.translation!!.length > 50 })
        errors.put("name", I18nFieldError(i18nKey = "programme.lumpSum.name.too.long"))

    if (lumpSum.description.any { it.translation != null && it.translation!!.length > 500 })
        errors.put("description", I18nFieldError(i18nKey = "programme.lumpSum.description.too.long"))

    val cost = lumpSum.cost
    if (cost == null || cost < BigDecimal.ZERO || cost > MAX_COST || cost.scale() > 2)
        errors.put("cost", I18nFieldError(i18nKey = "lump.sum.out.of.range"))

    if (lumpSum.phase == null)
        errors.put("phase", I18nFieldError(i18nKey = "lump.sum.phase.should.not.be.empty"))

    if (lumpSum.categories.size < 2)
        errors.put("categories", I18nFieldError(i18nKey = "programme.lumpSum.categories.min.2"))

    if (errors.isNotEmpty())
        throw I18nValidationException(
            i18nKey = "programme.lumpSum.invalid",
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nFieldErrors = errors,
        )
}
