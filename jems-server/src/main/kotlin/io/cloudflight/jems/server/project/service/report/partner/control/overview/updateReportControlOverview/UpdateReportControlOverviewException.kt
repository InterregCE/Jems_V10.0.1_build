package io.cloudflight.jems.server.project.service.report.partner.control.overview.updateReportControlOverview

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_REPORT_CONTROL_OVERVIEW_ERROR_CODE_PREFIX = "S-URCO"
private const val UPDATE_REPORT_CONTROL_OVERVIEW_ERROR_KEY_PREFIX = "use.case.update.report.control.overview"

class UpdateReportControlOverviewException(cause: Throwable) : ApplicationException(
    code = UPDATE_REPORT_CONTROL_OVERVIEW_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_REPORT_CONTROL_OVERVIEW_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

