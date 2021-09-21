package io.cloudflight.jems.server.common.exception

import io.cloudflight.jems.api.common.dto.I18nMessage

class ResourceNotFoundException(val entity: String? = null) : ApplicationNotFoundException(
    code = "$entity.not.found", i18nMessage = I18nMessage(i18nKey = entity)
)
