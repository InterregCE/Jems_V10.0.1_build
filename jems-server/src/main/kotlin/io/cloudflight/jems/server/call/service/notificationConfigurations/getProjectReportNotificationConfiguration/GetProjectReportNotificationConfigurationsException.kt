package io.cloudflight.jems.server.call.service.notificationConfigurations.getProjectReportNotificationConfiguration

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_REPORT_NOTIFICATION_CONFIGURATIONS_ERROR_CODE_PREFIX = "S-GPRNC"
private const val GET_PROJECT_REPORT_NOTIFICATION_CONFIGURATION_ERROR_KEY_PREFIX = "use.case.get.project.report.notification.configurations"

class GetProjectReportNotificationConfigurationsException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_REPORT_NOTIFICATION_CONFIGURATIONS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_REPORT_NOTIFICATION_CONFIGURATION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
