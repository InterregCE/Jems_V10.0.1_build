package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.partner.model.BaseBudgetEntry
import org.springframework.http.HttpStatus
import java.math.BigDecimal


private const val MAX_ALLOWED_AMOUNT = 300
private val MAX_ALLOWED_VALUE = BigDecimal.valueOf(999_999_999_99L, 2)

fun validateBudgetEntries(budgetDTOList: List<BaseBudgetEntry>) {

    if (budgetDTOList.size > MAX_ALLOWED_AMOUNT)
        throw I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nKey = "project.partner.budget.max.allowed.reached"
        )

    if (!budgetDTOList.parallelStream().allMatch {
            it.numberOfUnits <= MAX_ALLOWED_VALUE && it.pricePerUnit <= MAX_ALLOWED_VALUE
                && it.numberOfUnits.multiply(it.pricePerUnit) <= MAX_ALLOWED_VALUE
        })
        throw I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nKey = "project.partner.budget.number.out.of.range"
        )
}
