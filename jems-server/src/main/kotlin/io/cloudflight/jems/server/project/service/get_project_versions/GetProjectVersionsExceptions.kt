package io.cloudflight.jems.server.project.service.get_project_versions

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val GET_PROJECT_VERSIONS_ERROR_CODE_PREFIX = "S-GPV"
const val GET_PROJECT_VERSIONS_ERROR_KEY_PREFIX = "use.case.get.project.versions"

class GetProjectVersionsExceptions(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_VERSIONS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_VERSIONS_ERROR_KEY_PREFIX.failed"), cause = cause
)

