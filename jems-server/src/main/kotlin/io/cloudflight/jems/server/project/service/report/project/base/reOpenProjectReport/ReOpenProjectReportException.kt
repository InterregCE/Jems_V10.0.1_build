package io.cloudflight.jems.server.project.service.report.project.base.reOpenProjectReport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val ERROR_CODE_PREFIX = "S-RPR"
private const val ERROR_KEY_PREFIX = "use.case.reopen.project.report"

class ReOpenProjectReportException(cause: Throwable) : ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ReportCanNotBeReOpened : ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.not.submitted"),
)
