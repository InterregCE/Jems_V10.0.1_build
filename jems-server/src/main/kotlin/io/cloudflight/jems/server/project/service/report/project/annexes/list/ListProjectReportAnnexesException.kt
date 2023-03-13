package io.cloudflight.jems.server.project.service.report.project.annexes.list

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val LIST_PROJECT_REPORT_ANNEXES_FILES_ERROR_CODE_PREFIX = "S-LPRAF"
private const val LIST_PROJECT_REPORT_ANNEXES_FILES_ERROR_KEY_PREFIX = "use.case.list.project.report.annexes.files"

class ListProjectReportAnnexesException(cause: Throwable) : ApplicationException(
    code = LIST_PROJECT_REPORT_ANNEXES_FILES_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$LIST_PROJECT_REPORT_ANNEXES_FILES_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
