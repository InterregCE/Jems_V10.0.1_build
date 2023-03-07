package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.attachment.delete

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException


private const val DELETE_ATTACHMENT_FROM_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_CODE_PREFIX = "S-DAFPRRP"
private const val DELETE_ATTACHMENT_FROM_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_KEY_PREFIX = "use.case.delete.attachment.from.project.report.result.principle"

class DeleteAttachmentFromProjectReportResultPrincipleException(cause: Throwable) : ApplicationException(
    code = DELETE_ATTACHMENT_FROM_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_ATTACHMENT_FROM_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class FileNotFound : ApplicationUnprocessableException(
    code = "$DELETE_ATTACHMENT_FROM_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_ATTACHMENT_FROM_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_KEY_PREFIX.file.not.found"),
)
