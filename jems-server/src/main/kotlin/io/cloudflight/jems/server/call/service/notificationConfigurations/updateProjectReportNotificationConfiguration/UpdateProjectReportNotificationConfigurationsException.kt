package io.cloudflight.jems.server.call.service.notificationConfigurations.updateProjectReportNotificationConfiguration

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val ERROR_CODE_PREFIX = "S-UPRNC"
private const val ERROR_KEY_PREFIX = "use.case.update.project.report.notification.field.configurations"

class UpdateProjectReportNotificationConfigurationsException(cause: Throwable) : ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class InvalidNotificationTypeException(invalid: List<ProjectNotificationConfiguration>) : ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.invalid.project.notification.type"),
    message = invalid.joinToString(", ") { it.id.name },
)
