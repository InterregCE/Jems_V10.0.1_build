package io.cloudflight.jems.server.project.service.report.partner.identification.updateProjectPartnerReportIdentification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_PROJECT_PARTNER_REPORT_IDENTIFICATION_ERROR_CODE_PREFIX = "S-UPPRI"
private const val UPDATE_PROJECT_PARTNER_REPORT_IDENTIFICATION_ERROR_KEY_PREFIX = "use.case.update.project.partner.report.identification"

class UpdateProjectPartnerReportIdentificationException(cause: Throwable) : ApplicationException(
    code = UPDATE_PROJECT_PARTNER_REPORT_IDENTIFICATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_PARTNER_REPORT_IDENTIFICATION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ReportAlreadyClosed : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_PARTNER_REPORT_IDENTIFICATION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(i18nKey = "$UPDATE_PROJECT_PARTNER_REPORT_IDENTIFICATION_ERROR_KEY_PREFIX.report.already.closed"),
)

class InvalidPeriodNumber(periodNumber: Int) : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_PARTNER_REPORT_IDENTIFICATION_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(i18nKey = "$UPDATE_PROJECT_PARTNER_REPORT_IDENTIFICATION_ERROR_KEY_PREFIX.period.invalid"),
    message = "Period number $periodNumber is not valid."
)
