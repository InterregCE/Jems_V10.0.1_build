package io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerControlReportExpenditureVerification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_PARTNER_CONTROL_REPORT_EV_ERROR_CODE_PREFIX = "S-GPPCRE"
private const val GET_PROJECT_PARTNER_CONTROL_REPORT_EV_ERROR_KEY_PREFIX = "use.case.get.project.partner.control.report.expenditure"

class GetProjectPartnerControlReportExpenditureVerificationException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_PARTNER_CONTROL_REPORT_EV_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_PARTNER_CONTROL_REPORT_EV_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
