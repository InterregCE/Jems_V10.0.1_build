package io.cloudflight.jems.server.project.service.report.partner.export

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val PARTNER_REPORT_EXPORT_ERROR_CODE_PREFIX = "S-PPRE"
private const val PARTNER_REPORT_EXPORT_ERROR_KEY_PREFIX = "use.case.export.partner.report"
class ProjectPartnerReportExportException(cause: Throwable) : ApplicationException(
    code = PARTNER_REPORT_EXPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$PARTNER_REPORT_EXPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)