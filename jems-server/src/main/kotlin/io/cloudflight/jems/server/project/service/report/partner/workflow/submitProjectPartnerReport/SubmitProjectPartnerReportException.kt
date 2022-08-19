package io.cloudflight.jems.server.project.service.report.partner.workflow.submitProjectPartnerReport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val SUBMIT_PROJECT_PARTNER_REPORT_ERROR_CODE_PREFIX = "S-SPPR"
private const val SUBMIT_PROJECT_PARTNER_REPORT_ERROR_KEY_PREFIX = "use.case.submit.project.partner.report"

class SubmitProjectPartnerReportException(cause: Throwable) : ApplicationException(
    code = SUBMIT_PROJECT_PARTNER_REPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SUBMIT_PROJECT_PARTNER_REPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ReportAlreadyClosed : ApplicationUnprocessableException(
    code = "$SUBMIT_PROJECT_PARTNER_REPORT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$SUBMIT_PROJECT_PARTNER_REPORT_ERROR_KEY_PREFIX.report.already.closed"),
)

class CurrencyRatesMissing(missingRates: Set<String>) : ApplicationUnprocessableException(
    code = "$SUBMIT_PROJECT_PARTNER_REPORT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(
        "$SUBMIT_PROJECT_PARTNER_REPORT_ERROR_KEY_PREFIX.currency.rates.not.available.for.this.month",
        i18nArguments = mapOf("currencyCode" to missingRates.joinToString(", ")),
    ),
)
