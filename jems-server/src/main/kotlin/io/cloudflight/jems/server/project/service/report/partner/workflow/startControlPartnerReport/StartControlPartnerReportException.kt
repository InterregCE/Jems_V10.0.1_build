package io.cloudflight.jems.server.project.service.report.partner.workflow.startControlPartnerReport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val START_CONTROL_PARTNER_REPORT_ERROR_CODE_PREFIX = "S-SCPR"
private const val START_CONTROL_PARTNER_REPORT_ERROR_KEY_PREFIX = "use.case.start.control.partner.report"

class StartControlPartnerReportException(cause: Throwable) : ApplicationException(
    code = START_CONTROL_PARTNER_REPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$START_CONTROL_PARTNER_REPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ReportNotSubmitted : ApplicationUnprocessableException(
    code = "$START_CONTROL_PARTNER_REPORT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$START_CONTROL_PARTNER_REPORT_ERROR_KEY_PREFIX.report.not.submitted"),
)
