package io.cloudflight.jems.server.programme.service.language.update_languages

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationAccessDeniedException

const val UPDATE_LANGUAGES_ERROR_CODE_PREFIX = "S-UL"
const val UPDATE_LANGUAGES_ERROR_KEY_PREFIX = "use.case.update.languages"

class UpdateLanguagesWhenProgrammeSetupRestricted : ApplicationAccessDeniedException(
    code = "$UPDATE_LANGUAGES_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_LANGUAGES_ERROR_KEY_PREFIX.programme.setup.restricted"),
)
