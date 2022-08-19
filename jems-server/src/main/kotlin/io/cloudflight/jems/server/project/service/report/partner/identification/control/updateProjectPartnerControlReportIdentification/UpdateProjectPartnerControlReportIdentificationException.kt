package io.cloudflight.jems.server.project.service.report.partner.identification.control.updateProjectPartnerControlReportIdentification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_PROJECT_PARTNER_CONTROL_REPORT_IDENTIFICATION_ERROR_CODE_PREFIX = "S-UPPCRI"
private const val UPDATE_PROJECT_PARTNER_CONTROL_REPORT_IDENTIFICATION_ERROR_KEY_PREFIX = "use.case.update.project.partner.control.report.identification"

class UpdateProjectPartnerControlReportIdentificationException(cause: Throwable) : ApplicationException(
    code = UPDATE_PROJECT_PARTNER_CONTROL_REPORT_IDENTIFICATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_PARTNER_CONTROL_REPORT_IDENTIFICATION_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ReportNotInControl : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_PARTNER_CONTROL_REPORT_IDENTIFICATION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(i18nKey = "$UPDATE_PROJECT_PARTNER_CONTROL_REPORT_IDENTIFICATION_ERROR_KEY_PREFIX.report.not.in.control"),
)
