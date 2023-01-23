package io.cloudflight.jems.server.project.service.report.partner.control.file.setDescriptionToCertificate

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException



private const val SET_DESCRIPTION_TO_CONTROL_CERTIFICATE_FILE_ERROR_CODE_PREFIX = "S-SDTCCF"
private const val SET_DESCRIPTION_TO_CONTROL_CERTIFICATE_FILE_ERROR_KEY_PREFIX = "use.case.set.description.to.control.certificate.file"

class SetDescriptionToCertificateException(cause: Throwable): ApplicationException (
    code = SET_DESCRIPTION_TO_CONTROL_CERTIFICATE_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SET_DESCRIPTION_TO_CONTROL_CERTIFICATE_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)


