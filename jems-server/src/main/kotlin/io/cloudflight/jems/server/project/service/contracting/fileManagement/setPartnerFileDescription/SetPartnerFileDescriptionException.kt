package io.cloudflight.jems.server.project.service.contracting.fileManagement.setPartnerFileDescription

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val SET_DESCRIPTION_TO_PARTNER_FILE_ERROR_CODE_PREFIX = "S-SDTPF"
private const val SET_DESCRIPTION_TO_PARTNER_FILE_ERROR_KEY_PREFIX = "use.case.set.description.to.partner.file"

class SetDescriptionToPartnerFileException(cause: Throwable) : ApplicationException(
    code = SET_DESCRIPTION_TO_PARTNER_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SET_DESCRIPTION_TO_PARTNER_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationNotFoundException(
    code = "$SET_DESCRIPTION_TO_PARTNER_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$SET_DESCRIPTION_TO_PARTNER_FILE_ERROR_KEY_PREFIX.not.found"),
)
