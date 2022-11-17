package io.cloudflight.jems.server.project.service.report.partner.procurement.deleteProjectPartnerReportProcurement

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val DELETE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX = "S-DPPRP"
private const val DELETE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX = "use.case.delete.project.partner.report.procurement"

class DeleteProjectPartnerReportProcurementException(cause: Throwable) : ApplicationException(
    code = DELETE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ReportAlreadyClosed : ApplicationUnprocessableException(
    code = "$DELETE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(i18nKey = "$DELETE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.report.already.closed"),
)
