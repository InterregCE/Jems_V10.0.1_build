package io.cloudflight.jems.server.project.service.partner.create_project_partner

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val CREATE_PROJECT_PARTNER_ERROR_CODE_PREFIX = "S-CPP"
private const val CREATE_PROJECT_PARTNER_ERROR_KEY_PREFIX = "use.case.create.project.partner"

class CreateProjectPartnerException(cause: Throwable) : ApplicationException(
    code = CREATE_PROJECT_PARTNER_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CREATE_PROJECT_PARTNER_ERROR_KEY_PREFIX.failed"),
    cause = cause
)