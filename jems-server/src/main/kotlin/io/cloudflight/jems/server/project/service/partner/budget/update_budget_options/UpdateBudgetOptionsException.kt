package io.cloudflight.jems.server.project.service.partner.budget.update_budget_options

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val UPDATE_BUDGET_OPTIONS_ERROR_CODE_PREFIX = "S-PPB-UBO"
const val UPDATE_BUDGET_OPTIONS_ERROR_KEY_PREFIX = "use.case.update.budget.options"

class UpdateBudgetOptionsException(cause: Throwable) : ApplicationException(
    code = UPDATE_BUDGET_OPTIONS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_BUDGET_OPTIONS_ERROR_KEY_PREFIX.failed"), cause = cause
)


class InvalidFlatRateCombinationException(formErrors: Map<String, I18nMessage>) :
    ApplicationUnprocessableException(
        code = "$UPDATE_BUDGET_OPTIONS_ERROR_CODE_PREFIX-001",
        i18nMessage = I18nMessage("$UPDATE_BUDGET_OPTIONS_ERROR_KEY_PREFIX.flat.rate.combination.is.not.valid"),
        formErrors = formErrors, cause = null
    )

class InvalidFlatRateException(formErrors: Map<String, I18nMessage>) : ApplicationUnprocessableException(
    code = "$UPDATE_BUDGET_OPTIONS_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_BUDGET_OPTIONS_ERROR_KEY_PREFIX.invalid.flatRate"),
    formErrors = formErrors, cause = null
)

class UpdateBudgetOptionsWhenProjectContracted : ApplicationBadRequestException(
    code = "$UPDATE_BUDGET_OPTIONS_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$UPDATE_BUDGET_OPTIONS_ERROR_KEY_PREFIX.changes.not.allowed.when.project.is.contracted"),
    message = "When project is contracted, budget options cannot be enabled or disabled!"
)
