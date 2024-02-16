package io.cloudflight.jems.server.payments.service.account.finance.getAmountSummary

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val GET_AMOUNTS_SUMMARY_BY_PRIORITY_AXIS_PREFIX = "S-GSOA"
private const val GET_AMOUNTS_SUMMARY_BY_PRIORITY_AXIS_PREFIX_ERROR_KEY_PREFIX =
    "use.case.get.payment.account.amount.summary.by.priority.axis"

class GetPaymentAccountAmountSummaryException(cause: Throwable) : ApplicationException(
    code = GET_AMOUNTS_SUMMARY_BY_PRIORITY_AXIS_PREFIX,
    i18nMessage = I18nMessage("$GET_AMOUNTS_SUMMARY_BY_PRIORITY_AXIS_PREFIX_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class EcPaymentDraftNumberExceededException: ApplicationUnprocessableException(
    code = "$GET_AMOUNTS_SUMMARY_BY_PRIORITY_AXIS_PREFIX-001",
    i18nMessage = I18nMessage("$GET_AMOUNTS_SUMMARY_BY_PRIORITY_AXIS_PREFIX_ERROR_KEY_PREFIX.ec.payment.draft.number.exceeded"),
)
