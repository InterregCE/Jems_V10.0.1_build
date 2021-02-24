package io.cloudflight.jems.server.programme.service.fund.update_fund

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationAccessDeniedException
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val UPDATE_FUND_ERROR_CODE_PREFIX = "S-UF"
const val UPDATE_FUND_ERROR_KEY_PREFIX = "use.case.update.fund"

class MakingChangesWhenProgrammeSetupRestricted : ApplicationAccessDeniedException(
    code = "$UPDATE_FUND_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_FUND_ERROR_KEY_PREFIX.programme.setup.restricted"),
)

class MaxAllowedFundsReachedException(amount: Int) :
    ApplicationBadRequestException(
        code = "$UPDATE_FUND_ERROR_CODE_PREFIX-002",
        i18nMessage = I18nMessage("$UPDATE_FUND_ERROR_KEY_PREFIX.max.allowed.amount.reached"),
        message = "max allowed: $amount",
    )

class FundAbbreviationTooLong :
    ApplicationUnprocessableException(
        code = "$UPDATE_FUND_ERROR_CODE_PREFIX-003",
        i18nMessage = I18nMessage("$UPDATE_FUND_ERROR_KEY_PREFIX.abbreviation.too.long"),
    )

class FundDescriptionTooLong :
    ApplicationUnprocessableException(
        code = "$UPDATE_FUND_ERROR_CODE_PREFIX-004",
        i18nMessage = I18nMessage("$UPDATE_FUND_ERROR_KEY_PREFIX.description.too.long"),
    )

class FundNotFound : ApplicationNotFoundException(
    code = "$UPDATE_FUND_ERROR_CODE_PREFIX-005",
    i18nMessage = I18nMessage("$UPDATE_FUND_ERROR_KEY_PREFIX.not.found"),
)
