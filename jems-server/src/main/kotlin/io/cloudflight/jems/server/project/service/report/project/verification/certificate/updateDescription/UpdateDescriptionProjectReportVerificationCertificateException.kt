package io.cloudflight.jems.server.project.service.report.project.verification.certificate.updateDescription

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val UPDATE_DESCRIPTION_PROJECT_REPORT_VERIFICATION_CERTIFICATE_ERROR_CODE_PREFIX = "S-UDPRVC"
private const val UPDATE_DESCRIPTION_PROJECT_REPORT_VERIFICATION_CERTIFICATE_ERROR_KEY_PREFIX =
    "use.case.update.description.project.report.verification.certificate"

class UpdateDescriptionProjectReportVerificationCertificateException(cause: Throwable) : ApplicationException(
    code = UPDATE_DESCRIPTION_PROJECT_REPORT_VERIFICATION_CERTIFICATE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_DESCRIPTION_PROJECT_REPORT_VERIFICATION_CERTIFICATE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationNotFoundException(
    code = "$UPDATE_DESCRIPTION_PROJECT_REPORT_VERIFICATION_CERTIFICATE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_DESCRIPTION_PROJECT_REPORT_VERIFICATION_CERTIFICATE_ERROR_KEY_PREFIX.not.found"),
)
