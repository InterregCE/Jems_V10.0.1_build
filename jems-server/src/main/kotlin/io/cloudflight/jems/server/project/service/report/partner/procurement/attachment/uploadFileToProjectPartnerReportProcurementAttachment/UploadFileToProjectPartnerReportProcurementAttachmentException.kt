package io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.uploadFileToProjectPartnerReportProcurementAttachment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_ATTACHMENT_ERROR_CODE_PREFIX = "S-UFTPPRPA"
private const val UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_ATTACHMENT_ERROR_KEY_PREFIX =
    "use.case.upload.file.to.project.partner.report.procurement.attachment"

class UploadFileToProjectPartnerReportProcurementAttachmentException(cause: Throwable) : ApplicationException(
    code = UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_ATTACHMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_ATTACHMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ReportNotFound(reportId: Long) : ApplicationNotFoundException(
    code = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_ATTACHMENT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(
        i18nKey = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_ATTACHMENT_ERROR_KEY_PREFIX.report.not.found",
        i18nArguments = mapOf("reportId" to reportId.toString()),
    ),
)

class MaxAmountOfAttachmentReachedException(maxAmount: Int) : ApplicationUnprocessableException(
    code = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_ATTACHMENT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(i18nKey = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_ATTACHMENT_ERROR_KEY_PREFIX.max.amount.of.beneficials.reached"),
    message = "max allowed: $maxAmount",
)

class FileTypeNotSupported : ApplicationUnprocessableException(
    code = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_ATTACHMENT_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_ATTACHMENT_ERROR_KEY_PREFIX.type.not.supported")
)
