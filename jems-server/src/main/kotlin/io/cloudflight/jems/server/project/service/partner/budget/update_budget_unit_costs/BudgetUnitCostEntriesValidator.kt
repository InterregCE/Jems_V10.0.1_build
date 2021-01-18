package io.cloudflight.jems.server.project.service.partner.budget.update_budget_unit_costs

import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.service.partner.budget.BUDGET_COST_INVALID_SUM_ERROR_KEY
import io.cloudflight.jems.server.project.service.partner.budget.BUDGET_COST_MAX_NUMBER_OF_ENTRIES_ERROR_KEY
import io.cloudflight.jems.server.project.service.partner.budget.BUDGET_COST_VALUE_LIMIT_ERROR_KEY
import io.cloudflight.jems.server.project.service.partner.budget.MAX_ALLOWED_BUDGET_VALUE
import io.cloudflight.jems.server.project.service.partner.budget.MAX_ALLOWED_NUMBER_OF_BUDGET_ENTRIES
import io.cloudflight.jems.server.project.service.partner.budget.truncate
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

const val BUDGET_COST_MAX_2_DECIMALS = "project.partner.budget.max.2.decimals"

@Service
class BudgetUnitCostEntriesValidator {

    fun validate(budgetDTOList: List<BudgetUnitCostEntry>, projectUnitCostList: List<ProgrammeUnitCost>) {

        if (budgetDTOList.size > MAX_ALLOWED_NUMBER_OF_BUDGET_ENTRIES)
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = BUDGET_COST_MAX_NUMBER_OF_ENTRIES_ERROR_KEY
            )

        if (!budgetDTOList.stream().allMatch {
                it.numberOfUnits <= MAX_ALLOWED_BUDGET_VALUE && it.rowSum <= MAX_ALLOWED_BUDGET_VALUE
            })
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = BUDGET_COST_VALUE_LIMIT_ERROR_KEY
            )
        if (budgetDTOList.any { it.numberOfUnits.scale() > 2 })
            throw I18nValidationException(i18nKey = BUDGET_COST_MAX_2_DECIMALS)

        val unitCostPerUnitById = projectUnitCostList.associateBy({ it.id }, { it.costPerUnit })

        if (budgetDTOList.any {
                it.rowSum.compareTo(it.numberOfUnits.multiply(unitCostPerUnitById[it.unitCostId]).truncate()) != 0
            })
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = BUDGET_COST_INVALID_SUM_ERROR_KEY
            )
    }

}
