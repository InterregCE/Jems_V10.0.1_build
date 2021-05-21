package io.cloudflight.jems.server.project.service.partner.update_project_partner

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_PROJECT_PARTNER_ERROR_CODE_PREFIX = "S-UPP"
private const val UPDATE_PROJECT_PARTNER_ERROR_KEY_PREFIX = "use.case.update.project.partner"

class UpdateProjectPartnerException(cause: Throwable) : ApplicationException(
    code = "$UPDATE_PROJECT_PARTNER_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_PARTNER_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class UpdateProjectPartnerAddressesException(cause: Throwable) : ApplicationException(
    code = "$UPDATE_PROJECT_PARTNER_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_PARTNER_ERROR_KEY_PREFIX.addresses.failed"),
    cause = cause
)

class UpdateProjectPartnerContactsException(cause: Throwable) : ApplicationException(
    code = "$UPDATE_PROJECT_PARTNER_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_PARTNER_ERROR_KEY_PREFIX.contacts.failed"),
    cause = cause
)

class UpdateProjectPartnerMotivationException(cause: Throwable) : ApplicationException(
    code = "$UPDATE_PROJECT_PARTNER_ERROR_CODE_PREFIX-004",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_PARTNER_ERROR_KEY_PREFIX.motivation.failed"),
    cause = cause
)