package io.cloudflight.jems.server.project.service.report.partner.base.reOpenControlPartnerReport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val ERROR_CODE_PREFIX = "S-RCPR"
private const val ERROR_KEY_PREFIX = "use.case.reopen.control.partner.report"

class ReOpenControlPartnerReportException(cause: Throwable) : ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ReportCanNotBeReOpened : ApplicationUnprocessableException(
    code = "${ERROR_CODE_PREFIX}-001",
    i18nMessage = I18nMessage("${ERROR_KEY_PREFIX}.status.invalid"),
)

class ReportCertificateException : ApplicationUnprocessableException(
    code = "${ERROR_CODE_PREFIX}-002",
    i18nMessage = I18nMessage("${ERROR_KEY_PREFIX}.certificate.included"),
)
