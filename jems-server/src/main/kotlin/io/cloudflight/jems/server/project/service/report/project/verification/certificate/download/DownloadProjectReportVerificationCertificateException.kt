package io.cloudflight.jems.server.project.service.report.project.verification.certificate.download

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val DOWNLOAD_PROJECT_REPORT_VERIFICATION_CERTIFICATE_ERROR_CODE_PREFIX = "S-DPRVC"
private const val DOWNLOAD_PROJECT_REPORT_VERIFICATION_CERTIFICATE_ERROR_KEY_PREFIX = "use.case.download.project.report.verification.certificate"

class DownloadProjectReportVerificationCertificateException(cause: Throwable) : ApplicationException(
    code = DOWNLOAD_PROJECT_REPORT_VERIFICATION_CERTIFICATE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DOWNLOAD_PROJECT_REPORT_VERIFICATION_CERTIFICATE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationNotFoundException(
    code = "$DOWNLOAD_PROJECT_REPORT_VERIFICATION_CERTIFICATE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DOWNLOAD_PROJECT_REPORT_VERIFICATION_CERTIFICATE_ERROR_KEY_PREFIX.not.found"),
)
