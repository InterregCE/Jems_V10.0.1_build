package io.cloudflight.jems.server.project.service.associatedorganization.get_associated_organization

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_ASSOCIATED_ORGANIZATION_ERROR_CODE_PREFIX = "S-GPAO"
private const val GET_ASSOCIATED_ORGANIZATION_ERROR_KEY_PREFIX = "use.case.get.associated.organization"

class GetAssociatedOrganizationException(cause: Throwable) : ApplicationException(
    code = GET_ASSOCIATED_ORGANIZATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_ASSOCIATED_ORGANIZATION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)