package io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerReportExpenditure

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_PROJECT_PARTNER_REPORT_EC_ERROR_CODE_PREFIX = "S-UPPRE"
private const val UPDATE_PROJECT_PARTNER_REPORT_EC_ERROR_KEY_PREFIX = "use.case.update.project.partner.report.expenditure"

class UpdateProjectPartnerReportExpenditureException(cause: Throwable) : ApplicationException(
    code = UPDATE_PROJECT_PARTNER_REPORT_EC_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_PARTNER_REPORT_EC_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class MaxAmountOfExpendituresReached(maxAmount: Int) : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_PARTNER_REPORT_EC_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(i18nKey = "$UPDATE_PROJECT_PARTNER_REPORT_EC_ERROR_KEY_PREFIX.max.amount.of.expenditures.reached"),
    message = "max allowed: $maxAmount",
)

class ReportAlreadyClosed : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_PARTNER_REPORT_EC_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(i18nKey = "$UPDATE_PROJECT_PARTNER_REPORT_EC_ERROR_KEY_PREFIX.report.already.closed"),
)
