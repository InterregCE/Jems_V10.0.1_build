package io.cloudflight.jems.server.programme.service.costoption

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.programme.service.costoption.create_lump_sum.IdHasToBeNull
import io.cloudflight.jems.server.programme.service.costoption.create_lump_sum.MaxAllowedLumpSumsReached
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import java.math.BigDecimal
import io.cloudflight.jems.server.programme.service.costoption.update_lump_sum.LumpSumIsInvalid as UpdateLumpSumIsInvalid
import io.cloudflight.jems.server.programme.service.costoption.create_lump_sum.LumpSumIsInvalid as CreateLumpSumIsInvalid

private val MAX_COST = BigDecimal.valueOf(999_999_999_99, 2)
private const val MAX_ALLOWED_LUMP_SUMS = 100

fun validateCreateLumpSum(lumpSumToValidate: ProgrammeLumpSum, currentCount: Long) {
    if (lumpSumToValidate.id != 0L)
        throw IdHasToBeNull()
    if (currentCount >= MAX_ALLOWED_LUMP_SUMS)
        throw MaxAllowedLumpSumsReached()
    validateCommonLumpSum(lumpSum = lumpSumToValidate, false)
}

fun validateUpdateLumpSum(lumpSum: ProgrammeLumpSum) {
    validateCommonLumpSum(lumpSum = lumpSum, true)
}

private fun validateCommonLumpSum(lumpSum: ProgrammeLumpSum, isUpdate: Boolean) {
    val errors = mutableMapOf<String, I18nMessage>()

    val cost = lumpSum.cost
    if (cost == null || cost <= BigDecimal.ZERO || cost > MAX_COST)
        errors.put("cost", I18nMessage(i18nKey = "lump.sum.out.of.range"))

    if (lumpSum.categories.size < 2)
        errors.put("categories", I18nMessage(i18nKey = "programme.lumpSum.categories.min.2"))

    if (errors.isNotEmpty())
        if (isUpdate)
            throw UpdateLumpSumIsInvalid(errors = errors)
        else
            throw CreateLumpSumIsInvalid(errors = errors)
}
