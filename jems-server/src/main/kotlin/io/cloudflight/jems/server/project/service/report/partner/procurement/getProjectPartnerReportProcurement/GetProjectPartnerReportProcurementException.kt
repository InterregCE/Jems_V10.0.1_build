package io.cloudflight.jems.server.project.service.report.partner.procurement.getProjectPartnerReportProcurement

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX = "S-GPPRP"
private const val GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX = "use.case.get.project.partner.report.procurement"

class GetProjectPartnerReportProcurementException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class GetProjectPartnerReportProcurementsForSelectorException(cause: Throwable) : ApplicationException(
    code = "$GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX-FS",
    i18nMessage = I18nMessage("$GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.for.selector.failed"),
    cause = cause
)
