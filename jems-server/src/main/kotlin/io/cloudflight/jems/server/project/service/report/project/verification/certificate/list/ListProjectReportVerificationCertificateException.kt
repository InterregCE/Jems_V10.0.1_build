package io.cloudflight.jems.server.project.service.report.project.verification.certificate.list

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val LIST_PROJECT_REPORT_VERIFICATION_CERTIFICATE_ERROR_CODE_PREFIX = "S-LPRVC"
private const val LIST_PROJECT_REPORT_VERIFICATION_CERTIFICATE_ERROR_KEY_PREFIX = "use.case.list.project.report.verification.certificate"

class ListProjectReportVerificationCertificateException(cause: Throwable) : ApplicationException(
    code = LIST_PROJECT_REPORT_VERIFICATION_CERTIFICATE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$LIST_PROJECT_REPORT_VERIFICATION_CERTIFICATE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationNotFoundException(
    code = "$LIST_PROJECT_REPORT_VERIFICATION_CERTIFICATE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$LIST_PROJECT_REPORT_VERIFICATION_CERTIFICATE_ERROR_KEY_PREFIX.not.found"),
)
