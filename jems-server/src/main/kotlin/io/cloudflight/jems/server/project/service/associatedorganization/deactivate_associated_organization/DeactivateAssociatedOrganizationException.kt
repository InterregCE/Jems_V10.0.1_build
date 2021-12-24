package io.cloudflight.jems.server.project.service.associatedorganization.deactivate_associated_organization

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val DEACTIVATE_ASSOCIATED_ORGANIZATION_ERROR_CODE_PREFIX = "S-DAO"
private const val DEACTIVATE_ASSOCIATED_ORGANIZATION_ERROR_KEY_PREFIX = "use.case.deactivate.associated.organization"


class DeactivateAssociatedOrganizationException(cause: Throwable) : ApplicationException(
    code = DEACTIVATE_ASSOCIATED_ORGANIZATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DEACTIVATE_ASSOCIATED_ORGANIZATION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class AssociatedOrganizationCannotBeDeactivatedException : ApplicationUnprocessableException(
    code = "$DEACTIVATE_ASSOCIATED_ORGANIZATION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DEACTIVATE_ASSOCIATED_ORGANIZATION_ERROR_KEY_PREFIX.cannot.be.deactivated.in.the.current.status.of.project")
)
