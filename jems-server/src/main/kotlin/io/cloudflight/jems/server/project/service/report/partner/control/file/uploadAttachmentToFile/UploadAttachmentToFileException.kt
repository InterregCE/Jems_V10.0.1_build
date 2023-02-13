package io.cloudflight.jems.server.project.service.report.partner.control.file.uploadAttachmentToFile

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPLOAD_ATTACHMENT_TO_FILE_ERROR_CODE_PREFIX = "S-UATF"
private const val UPLOAD_ATTACHMENT_TO_FILE_ERROR_KEY_PREFIX = "use.case.upload.attachment.to.file"

class UploadAttachmentToFileException(cause: Throwable) : ApplicationException(
    code = UPLOAD_ATTACHMENT_TO_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPLOAD_ATTACHMENT_TO_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ControlFileNotFoundException(fileId: Long) : ApplicationNotFoundException(
    code = "$UPLOAD_ATTACHMENT_TO_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(
        "$UPLOAD_ATTACHMENT_TO_FILE_ERROR_KEY_PREFIX.not.found",
        i18nArguments = mapOf("fileId" to fileId.toString())
    ),
)

class FileTypeNotSupported : ApplicationUnprocessableException(
    code = "$UPLOAD_ATTACHMENT_TO_FILE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPLOAD_ATTACHMENT_TO_FILE_ERROR_KEY_PREFIX.type.not.supported")
)
