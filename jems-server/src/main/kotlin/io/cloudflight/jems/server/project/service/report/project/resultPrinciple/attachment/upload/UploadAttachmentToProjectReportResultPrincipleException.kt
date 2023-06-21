package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.attachment.upload

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPLOAD_ATTACHMENT_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_CODE_PREFIX = "S-UAPRRP"
private const val UPLOAD_ATTACHMENT_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_KEY_PREFIX = "use.case.uppload.attachment.project.report.result.principle"

class UploadAttachmentToProjectReportResultPrincipleException(cause: Throwable) : ApplicationException(
    code = UPLOAD_ATTACHMENT_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPLOAD_ATTACHMENT_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class FileTypeNotSupported : ApplicationUnprocessableException(
    code = "$UPLOAD_ATTACHMENT_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPLOAD_ATTACHMENT_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_KEY_PREFIX.type.not.supported")
)
