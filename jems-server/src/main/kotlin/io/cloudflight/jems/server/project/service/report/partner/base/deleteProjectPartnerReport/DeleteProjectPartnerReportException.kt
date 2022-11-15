package io.cloudflight.jems.server.project.service.report.partner.base.deleteProjectPartnerReport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val DELETE_PROJECT_PARTNER_REPORT_ERROR_CODE_PREFIX = "S-DEPPR"
private const val DELETE_PROJECT_PARTNER_REPORT_ERROR_KEY_PREFIX = "use.case.delete.project.partner.report"

class DeleteProjectPartnerReportException(cause: Throwable) : ApplicationException(
    code = DELETE_PROJECT_PARTNER_REPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_PROJECT_PARTNER_REPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class DeletionIsNotAllowedException : ApplicationUnprocessableException(
    code = "$DELETE_PROJECT_PARTNER_REPORT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_PROJECT_PARTNER_REPORT_ERROR_KEY_PREFIX.deletion.is.not.allowed"),
)
