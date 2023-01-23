package io.cloudflight.jems.server.project.service.report.partner.control.file.listCertificates

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val LIST_REPORT_CONTROL_CERTIFICATES_ERROR_CODE_PREFIX = "S-LRCC"
private const val LIST_REPORT_CONTROL_CERTIFICATES_ERROR_KEY_PREFIX = "use.case.list.report.control.certificates"

class ListReportControlCertificatesException(cause: Throwable) : ApplicationException(
    code = LIST_REPORT_CONTROL_CERTIFICATES_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$LIST_REPORT_CONTROL_CERTIFICATES_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
