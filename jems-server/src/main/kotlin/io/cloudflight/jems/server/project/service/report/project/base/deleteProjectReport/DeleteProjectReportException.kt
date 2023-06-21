package io.cloudflight.jems.server.project.service.report.project.base.deleteProjectReport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val DELETE_PROJECT_REPORT_ERROR_CODE_PREFIX = "S-DEPR"
private const val DELETE_PROJECT_REPORT_ERROR_KEY_PREFIX = "use.case.delete.project.report"

class DeleteProjectReportException(cause: Throwable) : ApplicationException(
    code = DELETE_PROJECT_REPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_PROJECT_REPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ClosedReportCannotBeDeleted : ApplicationUnprocessableException(
    code = "$DELETE_PROJECT_REPORT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$DELETE_PROJECT_REPORT_ERROR_KEY_PREFIX.closed.report.cannot.be.deleted"),
)
