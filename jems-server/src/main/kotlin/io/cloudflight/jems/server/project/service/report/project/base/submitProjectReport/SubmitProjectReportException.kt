package io.cloudflight.jems.server.project.service.report.project.base.submitProjectReport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val FINALIZE_CONTROL_PARTNER_REPORT_ERROR_CODE_PREFIX = "S-FCPR"
private const val FINALIZE_CONTROL_PARTNER_REPORT_ERROR_KEY_PREFIX = "use.case.finalize.control.partner.report"

class FinalizeControlPartnerReportException(cause: Throwable) : ApplicationException(
    code = FINALIZE_CONTROL_PARTNER_REPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$FINALIZE_CONTROL_PARTNER_REPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ReportNotInControl : ApplicationUnprocessableException(
    code = "$FINALIZE_CONTROL_PARTNER_REPORT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$FINALIZE_CONTROL_PARTNER_REPORT_ERROR_KEY_PREFIX.report.not.in.control"),
)
