package io.cloudflight.jems.server.call.service.notificationConfigurations.updateProjectReportNotificationConfiguration

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_PROJECT_REPORT_NOTIFICATION_CONFIGURATIONS_ERROR_CODE_PREFIX = "S-UPRNC"
private const val UPDATE_PROJECT_REPORT_NOTIFICATION_CONFIGURATIONS_ERROR_KEY_PREFIX = "use.case.update.project.report.notification.field.configurations"

class UpdateProjectReportNotificationConfigurationsException(cause: Throwable) : ApplicationException(
    code = UPDATE_PROJECT_REPORT_NOTIFICATION_CONFIGURATIONS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_REPORT_NOTIFICATION_CONFIGURATIONS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class InvalidNotificationTypeException : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_REPORT_NOTIFICATION_CONFIGURATIONS_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_REPORT_NOTIFICATION_CONFIGURATIONS_ERROR_KEY_PREFIX.invalid.project.notification.type"),
)

