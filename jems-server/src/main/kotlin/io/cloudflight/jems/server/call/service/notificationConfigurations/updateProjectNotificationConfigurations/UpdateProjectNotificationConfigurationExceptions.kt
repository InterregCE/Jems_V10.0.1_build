package io.cloudflight.jems.server.call.service.notificationConfigurations.updateProjectNotificationConfigurations

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_PROJECT_NOTIFICATION_CONFIGURATIONS_ERROR_CODE_PREFIX = "S-UPNC"
private const val UPDATE_PROJECT_NOTIFICATION_CONFIGURATIONS_ERROR_KEY_PREFIX =
    "use.case.update.project.notification.field.configurations"

class UpdateProjectNotificationConfigurationsException(cause: Throwable) : ApplicationException(
    code = UPDATE_PROJECT_NOTIFICATION_CONFIGURATIONS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_NOTIFICATION_CONFIGURATIONS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class InvalidNotificationTypeException : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_NOTIFICATION_CONFIGURATIONS_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_NOTIFICATION_CONFIGURATIONS_ERROR_KEY_PREFIX.invalid.project.notification.type"),
)

