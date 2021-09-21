package io.cloudflight.jems.server.project.service.application.set_application_as_ineligible

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val SET_APPLICATION_AS_INELIGIBLE_ERROR_CODE_PREFIX = "S-PA-SAAI"
private const val SET_APPLICATION_AS_INELIGIBLE_ERROR_KEY_PREFIX = "use.case.set.application.as.ineligible"

class SetApplicationAsIneligibleException(cause: Throwable) : ApplicationException(
    code = SET_APPLICATION_AS_INELIGIBLE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SET_APPLICATION_AS_INELIGIBLE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class EligibilityAssessmentMissing : ApplicationUnprocessableException(
    code = "$SET_APPLICATION_AS_INELIGIBLE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$SET_APPLICATION_AS_INELIGIBLE_ERROR_KEY_PREFIX.missing.eligibility.assessment"),
)
