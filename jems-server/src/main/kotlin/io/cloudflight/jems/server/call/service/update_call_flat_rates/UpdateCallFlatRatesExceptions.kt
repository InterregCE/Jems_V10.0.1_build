package io.cloudflight.jems.server.call.service.update_call_flat_rates

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_CALL_FLAT_RATES_ERROR_CODE_PREFIX = "S-UPC-FR"
private const val UPDATE_CALL_FLAT_RATES_ERROR_KEY_PREFIX = "use.case.update.call.flatRates"

class UpdateCallFlatRatesExceptions(cause: Throwable) : ApplicationException(
    code = UPDATE_CALL_FLAT_RATES_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_CALL_FLAT_RATES_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FlatRatesRemovedAfterCallPublished(removedFlatRates: Set<FlatRateType>) : ApplicationBadRequestException(
    code = "$UPDATE_CALL_FLAT_RATES_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_CALL_FLAT_RATES_ERROR_KEY_PREFIX.removing.or.updating.existing.is.forbidden"),
    message = "Following flat rates cannot be changed: $removedFlatRates"
)

class DuplicateFlatRateTypesDefined : ApplicationUnprocessableException(
    code = "$UPDATE_CALL_FLAT_RATES_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_CALL_FLAT_RATES_ERROR_KEY_PREFIX.flatRate.duplicates"),
    message = "In list of Flat Rates you requested there are duplicates."
)

class FlatRateOutOfBounds(formErrors: Map<String, I18nMessage>) : ApplicationUnprocessableException(
    code = "$UPDATE_CALL_FLAT_RATES_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$UPDATE_CALL_FLAT_RATES_ERROR_KEY_PREFIX.flatRate.out.of.bounds"),
    formErrors = formErrors,
)
