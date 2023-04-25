package io.cloudflight.jems.server.project.service.report.partner.control.overview.runControlPartnerReportPreSubmissionCheck

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val RUN_CONTROL_PARTNER_REPORT_CHECK_ERROR_CODE_PREFIX = "S-RCPRC"
private const val RUN_CONTROL_PARTNER_REPORT_CHECK_ERROR_KEY_PREFIX = "use.case.run.control.partner.report.pre.submission.check"

class RunControlPartnerReportPreSubmissionCheckException(cause: Throwable) : ApplicationException(
    code = RUN_CONTROL_PARTNER_REPORT_CHECK_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$RUN_CONTROL_PARTNER_REPORT_CHECK_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
