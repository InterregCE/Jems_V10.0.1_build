package io.cloudflight.jems.server.project.service.report.partner.expenditure.control.updateProjectPartnerReportExpenditureVerification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_PROJECT_PARTNER_CONTROL_REPORT_EV_ERROR_CODE_PREFIX = "S-UPPCRE"
private const val UPDATE_PROJECT_PARTNER_CONTROL_REPORT_EV_ERROR_KEY_PREFIX = "use.case.update.project.partner.control.report.expenditure"

class UpdateProjectPartnerControlReportExpenditureVerificationException(cause: Throwable) : ApplicationException(
    code = UPDATE_PROJECT_PARTNER_CONTROL_REPORT_EV_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_PARTNER_CONTROL_REPORT_EV_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class TypologyOfErrorMissing(invalidIds: Set<Long>) : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_PARTNER_CONTROL_REPORT_EV_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(
        i18nKey = "$UPDATE_PROJECT_PARTNER_CONTROL_REPORT_EV_ERROR_KEY_PREFIX.typology.cannot.be.null"
    ),
    formErrors = invalidIds.associateBy({ it.toString() }, { I18nMessage(
        "$UPDATE_PROJECT_PARTNER_CONTROL_REPORT_EV_ERROR_KEY_PREFIX.typology.cannot.be.null"
    ) }),
)
