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

class PartnerAbbreviationNotUnique(abbreviation: Map<String, String>) : ApplicationUnprocessableException(
    code = "$CREATE_PROJECT_PARTNER_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$CREATE_PROJECT_PARTNER_ERROR_KEY_PREFIX.abbreviation.not.unique", abbreviation),
)

class LeadPartnerAlreadyExists(args: Map<String, String>) : ApplicationUnprocessableException(
    code = "$CREATE_PROJECT_PARTNER_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$CREATE_PROJECT_PARTNER_ERROR_KEY_PREFIX.role.lead.already.existing", args),
)

class PartnerIsNotLead() : ApplicationUnprocessableException(
    code = "$CREATE_PROJECT_PARTNER_ERROR_CODE_PREFIX-004",
    i18nMessage = I18nMessage("$CREATE_PROJECT_PARTNER_ERROR_KEY_PREFIX.oldLeadPartnerId.is.not.lead"),
)

class MaximumNumberOfPartnersReached() : ApplicationUnprocessableException(
    code = "$CREATE_PROJECT_PARTNER_ERROR_CODE_PREFIX-005",
    i18nMessage = I18nMessage("$CREATE_PROJECT_PARTNER_ERROR_KEY_PREFIX.max.allowed.count.reached"),
)