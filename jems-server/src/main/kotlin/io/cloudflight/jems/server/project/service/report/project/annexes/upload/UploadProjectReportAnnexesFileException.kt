package io.cloudflight.jems.server.project.service.report.project.annexes.upload

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPLOAD_PROJECT_REPORT_ANNEXES_FILE_ERROR_CODE_PREFIX = "S-UPRAF"
private const val UPLOAD_PROJECT_REPORT_ANNEXES_FILE_ERROR_KEY_PREFIX = "use.case.upload.project.report.annexes.file"

class UploadProjectReportAnnexesFileException(cause: Throwable) : ApplicationException(
    code = UPLOAD_PROJECT_REPORT_ANNEXES_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPLOAD_PROJECT_REPORT_ANNEXES_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileAlreadyExists : ApplicationUnprocessableException(
    code = "$UPLOAD_PROJECT_REPORT_ANNEXES_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPLOAD_PROJECT_REPORT_ANNEXES_FILE_ERROR_KEY_PREFIX.already.exists")
)
