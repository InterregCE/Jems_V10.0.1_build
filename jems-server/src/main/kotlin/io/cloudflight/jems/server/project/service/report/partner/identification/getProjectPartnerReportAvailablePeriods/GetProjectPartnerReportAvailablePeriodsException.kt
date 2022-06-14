package io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportAvailablePeriods

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_PARTNER_REPORT_AVAILABLE_PERIODS_ERROR_CODE_PREFIX = "S-GPPRAP"
private const val GET_PROJECT_PARTNER_REPORT_AVAILABLE_PERIODS_KEY_PREFIX = "use.case.get.project.partner.report.available.periods"

class GetProjectPartnerReportAvailablePeriodsException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_PARTNER_REPORT_AVAILABLE_PERIODS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_PARTNER_REPORT_AVAILABLE_PERIODS_KEY_PREFIX.failed"),
    cause = cause,
)
