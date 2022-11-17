package io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.getProjectPartnerReportProcurementBeneficial

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_PARTNER_REPORT_PROCUREMENT_BENEFICIAL_ERROR_CODE_PREFIX = "S-GPPRPB"
private const val GET_PROJECT_PARTNER_REPORT_PROCUREMENT_BENEFICIAL_ERROR_KEY_PREFIX = "use.case.get.project.partner.report.procurement.beneficial"

class GetProjectPartnerReportProcurementBeneficialException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_PARTNER_REPORT_PROCUREMENT_BENEFICIAL_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_PARTNER_REPORT_PROCUREMENT_BENEFICIAL_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
