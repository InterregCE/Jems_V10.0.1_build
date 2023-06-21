package io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.uploadFileToProjectPartnerReportProcurementGdprAttachment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationAccessDeniedException
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT_ERROR_CODE_PREFIX = "S-UFTPPRPGA"
private const val UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT_ERROR_KEY_PREFIX =
    "use.case.upload.file.to.project.partner.report.procurement.gdpr.attachment"

class UploadFileToProjectPartnerReportProcurementGdprAttachmentException(cause: Throwable) : ApplicationException(
    code = UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ReportNotFound(reportId: Long) : ApplicationNotFoundException(
    code = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(
        i18nKey = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT_ERROR_KEY_PREFIX.report.not.found",
        i18nArguments = mapOf("reportId" to reportId.toString()),
    ),
)

class MaxAmountOfAttachmentReachedException(maxAmount: Int) : ApplicationUnprocessableException(
    code = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(i18nKey = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT_ERROR_KEY_PREFIX.max.amount.of.beneficials.reached"),
    message = "max allowed: $maxAmount",
)

class FileTypeNotSupported : ApplicationUnprocessableException(
    code = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT_ERROR_KEY_PREFIX.type.not.supported")
)

class FileAlreadyExists(fileName: String) : ApplicationUnprocessableException(
    code = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT_ERROR_CODE_PREFIX-004",
    i18nMessage = I18nMessage(
        i18nKey = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT_ERROR_KEY_PREFIX.file.already.exists",
        i18nArguments = mapOf("fileName" to fileName),
    ),
)

class SensitiveFileException: ApplicationAccessDeniedException(
    code = "$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT_ERROR_CODE_PREFIX-005",
    i18nMessage = I18nMessage("$UPLOAD_FILE_TO_PROJECT_PARTNER_REPORT_PROCUREMENT_GDPR_ATTACHMENT_ERROR_KEY_PREFIX.sensitive.failed"),
)
