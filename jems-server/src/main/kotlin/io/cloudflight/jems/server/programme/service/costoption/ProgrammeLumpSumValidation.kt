package io.cloudflight.jems.server.programme.service.costoption

import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import org.springframework.http.HttpStatus
import java.math.BigDecimal

private val MAX_COST = BigDecimal.valueOf(999_999_999_99, 2)
private const val MAX_ALLOWED_LUMP_SUMS = 100

fun validateCreateLumpSum(lumpSumToValidate: ProgrammeLumpSum, currentCount: Long) {
    if (lumpSumToValidate.id != 0L)
        throw I18nValidationException(i18nKey = "programme.lumpSum.id.not.allowed")
    if (currentCount >= MAX_ALLOWED_LUMP_SUMS)
        throw I18nValidationException(i18nKey = "programme.lumpSum.max.allowed.reached")
    validateCommonLumpSum(lumpSum = lumpSumToValidate)
}

fun validateUpdateLumpSum(lumpSum: ProgrammeLumpSum) {
    validateCommonLumpSum(lumpSum = lumpSum)
}

private fun validateCommonLumpSum(lumpSum: ProgrammeLumpSum) {
    val errors = mutableMapOf<String, I18nFieldError>()

    val cost = lumpSum.cost
    if (cost == null || cost <= BigDecimal.ZERO || cost > MAX_COST || cost.scale() > 2)
        errors.put("cost", I18nFieldError(i18nKey = "lump.sum.out.of.range"))

    if (lumpSum.categories.size < 2)
        errors.put("categories", I18nFieldError(i18nKey = "programme.lumpSum.categories.min.2"))

    if (errors.isNotEmpty())
        throw I18nValidationException(
            i18nKey = "programme.lumpSum.invalid",
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nFieldErrors = errors,
        )
}
