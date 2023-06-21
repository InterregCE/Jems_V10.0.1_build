package io.cloudflight.jems.server.project.service.report.partner.control.file.generateCertificate

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException


private const val GENERATE_REPORT_CONTROL_CERTIFICATE_ERROR_CODE_PREFIX = "S-GCRC"
private const val GENERATE_REPORT_CONTROL_CERTIFICATE_ERROR_KEY_PREFIX = "use.generate.control.report.certificate"

class GenerateReportControlCertificateException(cause: Throwable): ApplicationException(
    code = GENERATE_REPORT_CONTROL_CERTIFICATE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GENERATE_REPORT_CONTROL_CERTIFICATE_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
