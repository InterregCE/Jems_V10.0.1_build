package io.cloudflight.jems.server.project.repository.workpackage.activity

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

const val WORK_PACKAGE_ACTIVITY_ERROR_CODE_PREFIX = "P-WP-A"

class WorkPackageActivityNotFoundException : ApplicationNotFoundException(
    code = "$WORK_PACKAGE_ACTIVITY_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("work.package.activity.not.found"), cause = null
)

