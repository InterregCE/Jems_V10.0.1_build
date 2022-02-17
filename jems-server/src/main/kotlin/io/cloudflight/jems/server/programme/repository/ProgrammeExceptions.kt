package io.cloudflight.jems.server.programme.repository

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

const val PROGRAMME_ERROR_CODE_PREFIX = "P-PR"
const val PROGRAMME_ERROR_KEY_PREFIX = "programme"


class ProgrammeLegalStatusNotFoundException : ApplicationNotFoundException(
    code = "$PROGRAMME_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$PROGRAMME_ERROR_KEY_PREFIX.legal.status.not.found")
)

class ProgrammeExportMetaDataNotFoundException : ApplicationNotFoundException(
    code = "$PROGRAMME_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$PROGRAMME_ERROR_KEY_PREFIX.export.metadata.not.found")
)
