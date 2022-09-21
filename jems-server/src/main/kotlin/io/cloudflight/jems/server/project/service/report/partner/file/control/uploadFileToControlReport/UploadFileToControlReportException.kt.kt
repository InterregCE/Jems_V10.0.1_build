package io.cloudflight.jems.server.project.service.report.partner.file.control.uploadFileToControlReport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPLOAD_FILE_TO_CONTROL_REPORT_ERROR_CODE_PREFIX = "S-UFPPCR"
private const val UPLOAD_FILE_TO_CONTROL_REPORT_ERROR_KEY_PREFIX = "use.case.upload.file.to.project.partner.control.report"

class UploadFileToControlReportException(cause: Throwable) : ApplicationException(
    code = UPLOAD_FILE_TO_CONTROL_REPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPLOAD_FILE_TO_CONTROL_REPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ReportNotInControl : ApplicationUnprocessableException(
    code = "$UPLOAD_FILE_TO_CONTROL_REPORT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(i18nKey = "$UPLOAD_FILE_TO_CONTROL_REPORT_ERROR_KEY_PREFIX.report.not.in.control"),
)

class FileTypeNotSupported : ApplicationUnprocessableException(
    code = "$UPLOAD_FILE_TO_CONTROL_REPORT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPLOAD_FILE_TO_CONTROL_REPORT_ERROR_KEY_PREFIX.type.not.supported")
)
