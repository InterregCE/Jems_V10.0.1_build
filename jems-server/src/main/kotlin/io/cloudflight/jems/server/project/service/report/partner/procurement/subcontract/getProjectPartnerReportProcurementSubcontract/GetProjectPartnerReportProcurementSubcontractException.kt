package io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.getProjectPartnerReportProcurementSubcontract

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_PARTNER_REPORT_PROCUREMENT_SUBCONTRACT_ERROR_CODE_PREFIX = "S-GPPRPS"
private const val GET_PROJECT_PARTNER_REPORT_PROCUREMENT_SUBCONTRACT_ERROR_KEY_PREFIX = "use.case.get.project.partner.report.procurement.subcontract"

class GetProjectPartnerReportProcurementSubcontractException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_PARTNER_REPORT_PROCUREMENT_SUBCONTRACT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_PARTNER_REPORT_PROCUREMENT_SUBCONTRACT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
