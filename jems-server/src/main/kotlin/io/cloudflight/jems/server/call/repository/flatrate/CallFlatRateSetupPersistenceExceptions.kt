package io.cloudflight.jems.server.call.repository.flatrate

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

const val CALL_FLAT_RATE_SETUP_PERSISTENCE_ERROR_CODE_PREFIX = "R-CFR-FRS"

class ProjectPartnerNotFoundException : ApplicationNotFoundException(
    code = "$CALL_FLAT_RATE_SETUP_PERSISTENCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("projectPartner.not.exist"), cause = null
)
class CallNotFoundException : ApplicationNotFoundException(
    code = "$CALL_FLAT_RATE_SETUP_PERSISTENCE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("call.not.exist"), cause = null
)
