package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.partner.model.BaseBudgetEntry
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.math.BigDecimal

const val MAX_ALLOWED_NUMBER_OF_BUDGET_ENTRIES = 300
const val BUDGET_COST_MAX_NUMBER_OF_ENTRIES_ERROR_KEY = "project.partner.budget.max.allowed.reached"
const val BUDGET_COST_VALUE_LIMIT_ERROR_KEY = "project.partner.budget.number.out.of.range"
val MAX_ALLOWED_BUDGET_VALUE: BigDecimal = BigDecimal.valueOf(999_999_999_99L, 2)

@Service
class BudgetCostEntriesValidator {

    fun validate(budgetDTOList: List<BaseBudgetEntry>) {

        if (budgetDTOList.size > MAX_ALLOWED_NUMBER_OF_BUDGET_ENTRIES)
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = BUDGET_COST_MAX_NUMBER_OF_ENTRIES_ERROR_KEY
            )

        if (!budgetDTOList.parallelStream().allMatch {
                it.numberOfUnits <= MAX_ALLOWED_BUDGET_VALUE && it.pricePerUnit <= MAX_ALLOWED_BUDGET_VALUE
                    && it.rowSum <= MAX_ALLOWED_BUDGET_VALUE
            })
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = BUDGET_COST_VALUE_LIMIT_ERROR_KEY
            )
    }

}
