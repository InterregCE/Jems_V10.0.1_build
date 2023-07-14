package io.cloudflight.jems.server.project.service.report.partner.base.getMyProjectPartnerReports

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_MY_PROJECT_PARTNER_REPORTS_ERROR_CODE_PREFIX = "S-GMPPR"
private const val GET_MY_PROJECT_PARTNER_REPORTS_ERROR_KEY_PREFIX = "use.case.get.my.project.partner.reports"

class GetMyProjectPartnerReportsException(cause: Throwable) : ApplicationException(
    code = GET_MY_PROJECT_PARTNER_REPORTS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_MY_PROJECT_PARTNER_REPORTS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
