package io.cloudflight.jems.server.project.service.report.partner.control.file.deleteReportControlCertificateAttachment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val DELETE_REPORT_CONTROL_CERTIFICATE_ATTACHMENT_ERROR_CODE_PREFIX = "S-DLRCCA"
private const val DELETE_REPORT_CONTROL_CERTIFICATE_ATTACHMENT_ERROR_KEY_PREFIX = "use.case.delete.report.control.certificate.attachment"

class DeleteReportControlCertificateAttachmentException (cause: Throwable) : ApplicationException(
    code = DELETE_REPORT_CONTROL_CERTIFICATE_ATTACHMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_REPORT_CONTROL_CERTIFICATE_ATTACHMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationNotFoundException(
    code = "$DELETE_REPORT_CONTROL_CERTIFICATE_ATTACHMENT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_REPORT_CONTROL_CERTIFICATE_ATTACHMENT_ERROR_KEY_PREFIX.not.found")
)

class DeletionNotAllowed : ApplicationBadRequestException(
    code = "$DELETE_REPORT_CONTROL_CERTIFICATE_ATTACHMENT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$DELETE_REPORT_CONTROL_CERTIFICATE_ATTACHMENT_ERROR_KEY_PREFIX.deletion.after.certified")
)
