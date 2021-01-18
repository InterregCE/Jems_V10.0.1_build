package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.partner.model.BaseBudgetEntry
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.math.BigDecimal

const val MAX_ALLOWED_NUMBER_OF_BUDGET_ENTRIES = 300
const val BUDGET_COST_MAX_NUMBER_OF_ENTRIES_ERROR_KEY = "project.partner.budget.max.allowed.reached"
const val BUDGET_COST_DUPLICATE_ENTRY_ERROR_KEY = "project.partner.budget.duplicate.entry.error"
const val BUDGET_COST_VALUE_LIMIT_ERROR_KEY = "project.partner.budget.number.out.of.range"
const val BUDGET_COST_INVALID_SUM_ERROR_KEY = "project.partner.budget.sum.not.match"
const val BUDGET_COST_INVALID_SUM_SCALE_ERROR_KEY = "project.partner.budget.sum.invalid.scale"
const val BUDGET_COST_INVALID_NUMBER_OF_UNITS_SCALE_ERROR_KEY = "project.partner.budget.number.of.units.invalid.scale"
const val BUDGET_COST_INVALID_PRICE_PER_UNIT_SCALE_ERROR_KEY = "project.partner.budget.price.per.unit.invalid.scale"
val MAX_ALLOWED_BUDGET_VALUE: BigDecimal = BigDecimal.valueOf(999_999_999_99L, 2)

@Service
class BudgetCostEntriesValidator {

    fun validate(budgetEntries: List<BaseBudgetEntry>) {

        if (budgetEntries.size > MAX_ALLOWED_NUMBER_OF_BUDGET_ENTRIES)
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = BUDGET_COST_MAX_NUMBER_OF_ENTRIES_ERROR_KEY
            )

        if (budgetEntries.size != budgetEntries.distinctBy { it.id }.size)
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = BUDGET_COST_DUPLICATE_ENTRY_ERROR_KEY
            )
        budgetEntries.stream().forEach {
            if (it.rowSum.scale() > 2)
                throw I18nValidationException(
                    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                    i18nKey = BUDGET_COST_INVALID_SUM_SCALE_ERROR_KEY
                )

            if (it.numberOfUnits.scale() > 2)
                throw I18nValidationException(
                    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                    i18nKey = BUDGET_COST_INVALID_NUMBER_OF_UNITS_SCALE_ERROR_KEY
                )

            if (it.pricePerUnit.scale() > 2)
                throw I18nValidationException(
                    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                    i18nKey = BUDGET_COST_INVALID_PRICE_PER_UNIT_SCALE_ERROR_KEY
                )

            if (it.numberOfUnits >= MAX_ALLOWED_BUDGET_VALUE || it.pricePerUnit >= MAX_ALLOWED_BUDGET_VALUE
                || it.rowSum >= MAX_ALLOWED_BUDGET_VALUE
            )
                throw I18nValidationException(
                    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                    i18nKey = BUDGET_COST_VALUE_LIMIT_ERROR_KEY
                )

            if (it.rowSum.compareTo(
                    it.numberOfUnits.multiply(it.pricePerUnit).truncate()
                ) != 0
            )
                throw I18nValidationException(
                    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                    i18nKey = BUDGET_COST_INVALID_SUM_ERROR_KEY
                )
        }

    }
}
