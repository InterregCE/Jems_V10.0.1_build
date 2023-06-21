package io.cloudflight.jems.server.call.service.notificationConfigurations.getProjectNotificationConfiguration

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_NOTIFICATION_CONFIGURATIONS_ERROR_CODE_PREFIX = "S-GPNC"
private const val GET_PROJECT_NOTIFICATION_CONFIGURATION_ERROR_KEY_PREFIX = "use.case.get.project.notification.configurations"

class GetProjectNotificationConfigurationException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_NOTIFICATION_CONFIGURATIONS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_NOTIFICATION_CONFIGURATION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
