package io.cloudflight.jems.server.project.service.report.project.verification.certificate.generate

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException


private const val GENERATE_REPORT_VERIFICATION_CERTIFICATE_ERROR_CODE_PREFIX = "S-GVRC"
private const val GENERATE_REPORT_VERIFICATION_CERTIFICATE_ERROR_KEY_PREFIX = "use.generate.verification.report.certificate"

class GenerateVerificationCertificateException(cause: Throwable): ApplicationException(
    code = GENERATE_REPORT_VERIFICATION_CERTIFICATE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GENERATE_REPORT_VERIFICATION_CERTIFICATE_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

