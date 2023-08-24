package io.cloudflight.jems.server.project.service.report.partner.control.expenditure.getProjectPartnerReportExpenditureVerification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_PARTNER_CONTROL_REPORT_PEI_ERROR_CODE_PREFIX = "S-GPPCRPEI"
private const val GET_PROJECT_PARTNER_CONTROL_REPORT_PEI_ERROR_KEY_PREFIX = "use.case.get.project.partner.control.report.parked.expenditure.ids"

class GetProjectPartnerControlReportParkedExpenditureIdsException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_PARTNER_CONTROL_REPORT_PEI_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_PARTNER_CONTROL_REPORT_PEI_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
