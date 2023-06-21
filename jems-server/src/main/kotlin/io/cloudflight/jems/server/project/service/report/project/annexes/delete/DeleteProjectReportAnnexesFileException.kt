package io.cloudflight.jems.server.project.service.report.project.annexes.delete

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val DELETE_PROJECT_REPORT_ANNEXES_FILE_ERROR_CODE_PREFIX = "S-DPRAF"
private const val DELETE_PROJECT_REPORT_ANNEXES_FILE_ERROR_KEY_PREFIX = "use.case.delete.project.report.annexes.file"

class DeleteProjectReportAnnexesFileException(cause: Throwable) : ApplicationException(
    code = DELETE_PROJECT_REPORT_ANNEXES_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_PROJECT_REPORT_ANNEXES_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationUnprocessableException(
    code = "$DELETE_PROJECT_REPORT_ANNEXES_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_PROJECT_REPORT_ANNEXES_FILE_ERROR_KEY_PREFIX.not.found")
)
