package io.cloudflight.jems.server.project.service.report.project.certificate.deselectCertificate

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val ERROR_CODE_PREFIX = "S-DC"
private const val ERROR_KEY_PREFIX = "use.case.deselect.certificate"

class DeselectCertificateException(cause: Throwable) : ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class CertificatesCannotBeChangedWhenReOpenModeIsLimited : ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.forbidden.when.reOpen.mode.limited"),
)
