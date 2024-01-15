package io.cloudflight.jems.server.project.service.report.project.verification.file.delete

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val DELETE_PROJECT_REPORT_VERIFICATION_FILE_ERROR_CODE_PREFIX = "S-DPPVF"
private const val DELETE_PROJECT_REPORT_VERIFICATION_FILE_ERROR_KEY_PREFIX = "use.case.delete.project.report.verification.file"

class DeleteProjectReportVerificationFileException(cause: Throwable) : ApplicationException(
    code = DELETE_PROJECT_REPORT_VERIFICATION_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_PROJECT_REPORT_VERIFICATION_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationNotFoundException(
    code = "$DELETE_PROJECT_REPORT_VERIFICATION_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_PROJECT_REPORT_VERIFICATION_FILE_ERROR_KEY_PREFIX.not.found"),
)

class VerificationReportNotOngoing: ApplicationUnprocessableException(
    code = "$DELETE_PROJECT_REPORT_VERIFICATION_FILE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$DELETE_PROJECT_REPORT_VERIFICATION_FILE_ERROR_KEY_PREFIX.report.not.ongoing"),
)
