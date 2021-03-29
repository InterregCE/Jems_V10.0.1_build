package io.cloudflight.jems.server.programme.service.fund.update_funds

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val UPDATE_FUNDS_ERROR_CODE_PREFIX = "S-UF"
const val UPDATE_FUNDS_ERROR_KEY_PREFIX = "use.case.update.funds"

class UpdateFundsFailed(cause: Throwable) : ApplicationException(
    code = UPDATE_FUNDS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_FUNDS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ChangesAreNotAllowedException : ApplicationUnprocessableException(
    code = "$UPDATE_FUNDS_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_FUNDS_ERROR_KEY_PREFIX.changes.are.not.allowed.since.programme.setup.is.restricted"),
)

class FundNotFoundException : ApplicationNotFoundException(
    code = "$UPDATE_FUNDS_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_FUNDS_ERROR_KEY_PREFIX.fund.not.found"),
)

class CreationOfFundUnderPreDefinedTypesIsNotAllowedException : ApplicationUnprocessableException(
    code = "$UPDATE_FUNDS_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$UPDATE_FUNDS_ERROR_KEY_PREFIX.creation.of.fund.under.pre.defined.types.is.not.allowed"),
)
