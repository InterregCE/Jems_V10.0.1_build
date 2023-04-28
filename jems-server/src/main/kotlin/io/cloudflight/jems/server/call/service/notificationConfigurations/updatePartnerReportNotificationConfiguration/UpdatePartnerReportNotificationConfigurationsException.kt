package io.cloudflight.jems.server.call.service.notificationConfigurations.updatePartnerReportNotificationConfiguration

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_PARTNER_REPORT_NOTIFICATION_CONFIGURATIONS_ERROR_CODE_PREFIX = "S-UPRNC"
private const val UPDATE_PARTNER_REPORT_NOTIFICATION_CONFIGURATIONS_ERROR_KEY_PREFIX = "use.case.update.partner.report.notification.field.configurations"

class UpdatePartnerReportNotificationConfigurationsException(cause: Throwable) : ApplicationException(
    code = UPDATE_PARTNER_REPORT_NOTIFICATION_CONFIGURATIONS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PARTNER_REPORT_NOTIFICATION_CONFIGURATIONS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class InvalidNotificationTypeException : ApplicationUnprocessableException(
    code = "$UPDATE_PARTNER_REPORT_NOTIFICATION_CONFIGURATIONS_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_PARTNER_REPORT_NOTIFICATION_CONFIGURATIONS_ERROR_KEY_PREFIX.invalid.project.notification.type"),
)

