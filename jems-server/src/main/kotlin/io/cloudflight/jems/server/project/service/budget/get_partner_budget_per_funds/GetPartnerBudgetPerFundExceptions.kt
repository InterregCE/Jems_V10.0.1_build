package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PARTNER_BUDGET_FUND_ERROR_CODE_PREFIX = "S-GPBFP"
private const val GET_PARTNER_BUDGET_FUND_ERROR_KEY_PREFIX = "use.case.get.partner.budget.per.fund"

class GetPartnerBudgetPerFundExceptions(cause: Throwable) : ApplicationException(
    code = GET_PARTNER_BUDGET_FUND_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PARTNER_BUDGET_FUND_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
