package io.cloudflight.jems.server.payments.service.advance.getContractedProjects

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_CONTRACTED_PROJECTS_ERROR_CODE_PREFIX = "S-GCP"
private const val GET_CONTRACTED_PROJECTS_ERROR_KEY_PREFIX = "use.case.get.contracted.projects"

class GetContractedProjectsException(cause: Throwable) : ApplicationException(
    code = GET_CONTRACTED_PROJECTS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_CONTRACTED_PROJECTS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
