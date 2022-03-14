package io.cloudflight.jems.server.project.service.report.partner.partnerReportExpenditureCosts

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_PROJECT_PARTNER_REPORT_EC_ERROR_CODE_PREFIX = "S-UPREC"
private const val UPDATE_PROJECT_PARTNER_REPORT_EC_ERROR_KEY_PREFIX = "use.case.update.partner.report.expenditure.costs"

class PartnerReportExpenditureCostsException(cause: Throwable) : ApplicationException(
    code = UPDATE_PROJECT_PARTNER_REPORT_EC_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_PARTNER_REPORT_EC_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
