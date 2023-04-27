package io.cloudflight.jems.server.project.service.associatedorganization

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val PROJECT_ASSOCIATED_ORGANIZATION_ERROR_CODE_PREFIX = "S-PAO"
private const val PROJECT_ASSOCIATED_ORGANIZATION_ERROR_KEY_PREFIX = "use.case.project.associated.organization"

class MaximumNumberOfOrganizationsReached(maxAmount: Int) : ApplicationUnprocessableException(
    code = "$PROJECT_ASSOCIATED_ORGANIZATION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(
        "$PROJECT_ASSOCIATED_ORGANIZATION_ERROR_KEY_PREFIX.max.allowed.active.count.reached",
        mapOf("maxAmount" to maxAmount.toString())
    )
)
