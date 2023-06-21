package io.cloudflight.jems.server.project.service.associatedorganization

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val PROJECT_ASSOCIATED_ORGANIZATION_ERROR_CODE_PREFIX = "S-PAO"
private const val PROJECT_ASSOCIATED_ORGANIZATION_ERROR_KEY_PREFIX = "use.case.project.associated.organization"

class ProjectAssociatedOrganizationException(cause: Throwable) : ApplicationException(
    code = PROJECT_ASSOCIATED_ORGANIZATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$PROJECT_ASSOCIATED_ORGANIZATION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class MaximumNumberOfOrganizationsReached(maxSize: Int) : ApplicationUnprocessableException(
    code = "$PROJECT_ASSOCIATED_ORGANIZATION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(
        "$PROJECT_ASSOCIATED_ORGANIZATION_ERROR_KEY_PREFIX.max.allowed.active.count.reached",
        mapOf("maxSize" to maxSize.toString())
    )
)
