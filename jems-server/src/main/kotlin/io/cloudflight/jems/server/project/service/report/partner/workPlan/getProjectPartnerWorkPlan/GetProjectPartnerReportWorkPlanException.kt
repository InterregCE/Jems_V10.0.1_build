package io.cloudflight.jems.server.project.service.report.partner.workPlan.getProjectPartnerWorkPlan

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_PARTNER_REPORT_WP_ERROR_CODE_PREFIX = "S-GPPRWP"
private const val GET_PROJECT_PARTNER_REPORT_WP_ERROR_KEY_PREFIX = "use.case.get.project.partner.report.work.plan"

class GetProjectPartnerReportWorkPlanException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_PARTNER_REPORT_WP_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_PARTNER_REPORT_WP_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
