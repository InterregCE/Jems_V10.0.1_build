package io.cloudflight.jems.server.project.service.report.partner.control.file.downloadCertificateAttachment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val DOWNLOAD_REPORT_CONTROL_CERTIFICATE_ATTACHMENT_ERROR_CODE_PREFIX = "S-DRCCA"
private const val DOWNLOAD_REPORT_CONTROL_CERTIFICATE_ATTACHMENT_ERROR_KEY_PREFIX = "use.case.download.report.control.certificate.attachment"

class DownloadReportControlCertificateAttachmentException (cause: Throwable) : ApplicationException(
    code = DOWNLOAD_REPORT_CONTROL_CERTIFICATE_ATTACHMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DOWNLOAD_REPORT_CONTROL_CERTIFICATE_ATTACHMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationNotFoundException(
    code = "$DOWNLOAD_REPORT_CONTROL_CERTIFICATE_ATTACHMENT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DOWNLOAD_REPORT_CONTROL_CERTIFICATE_ATTACHMENT_ERROR_KEY_PREFIX.not.found")
)
