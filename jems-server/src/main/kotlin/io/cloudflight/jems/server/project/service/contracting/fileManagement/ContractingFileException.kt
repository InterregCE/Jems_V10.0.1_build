package io.cloudflight.jems.server.project.service.contracting.fileManagement

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val CONTRACTING_FILE_ERROR_KEY_PREFIX = "use.case.contracting.file"

class FileNotFound : ApplicationNotFoundException(
    code = "S-CF",
    i18nMessage = I18nMessage("$CONTRACTING_FILE_ERROR_KEY_PREFIX.not.found"),
)
