package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val WORK_PACKAGE_ERROR_CODE_PREFIX = "S-WP"
private const val WORK_PACKAGE_ERROR_KEY_PREFIX = "use.case.work.package"

class WorkPackageDeactivationNotAllowedException : ApplicationUnprocessableException(
    code = "$WORK_PACKAGE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$WORK_PACKAGE_ERROR_KEY_PREFIX.deactivation.not.allowed.since.project.is.not.contracted"),
)
