package io.cloudflight.jems.server.project.service.partner.create_project_partner

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val CREATE_PROJECT_PARTNER_ERROR_CODE_PREFIX = "S-CPP"
private const val CREATE_PROJECT_PARTNER_ERROR_KEY_PREFIX = "use.case.create.project.partner"

class CreateProjectPartnerException(cause: Throwable) : ApplicationException(
    code = CREATE_PROJECT_PARTNER_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CREATE_PROJECT_PARTNER_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class MaximumNumberOfPartnersReached(maxAmount: Int) : ApplicationUnprocessableException(
    code = "$CREATE_PROJECT_PARTNER_ERROR_CODE_PREFIX-005",
    i18nMessage = I18nMessage("$CREATE_PROJECT_PARTNER_ERROR_KEY_PREFIX.max.allowed.count.reached", mapOf("maxAmount" to maxAmount.toString())),
)

class MaximumNumberOfActivePartnersReached(maxAmount: Int) : ApplicationUnprocessableException(
    code = "$CREATE_PROJECT_PARTNER_ERROR_CODE_PREFIX-006",
    i18nMessage = I18nMessage("$CREATE_PROJECT_PARTNER_ERROR_KEY_PREFIX.max.allowed.active.count.reached", mapOf("maxAmount" to maxAmount.toString()))
)
