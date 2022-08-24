package io.cloudflight.jems.server.project.service.report.partner.identification.control.getProjectPartnerControlReportIdentification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val GET_PROJECT_PARTNER_CONTROL_REPORT_IDENTIFICATION_ERROR_CODE_PREFIX = "S-GPPCRI"
private const val GET_PROJECT_PARTNER_CONTROL_REPORT_IDENTIFICATION_ERROR_KEY_PREFIX = "use.case.get.project.partner.control.report.identification"

class GetProjectPartnerControlReportIdentificationException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_PARTNER_CONTROL_REPORT_IDENTIFICATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_PARTNER_CONTROL_REPORT_IDENTIFICATION_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ReportNotInControl : ApplicationUnprocessableException(
    code = "$GET_PROJECT_PARTNER_CONTROL_REPORT_IDENTIFICATION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(i18nKey = "$GET_PROJECT_PARTNER_CONTROL_REPORT_IDENTIFICATION_ERROR_KEY_PREFIX.report.not.in.control"),
)
