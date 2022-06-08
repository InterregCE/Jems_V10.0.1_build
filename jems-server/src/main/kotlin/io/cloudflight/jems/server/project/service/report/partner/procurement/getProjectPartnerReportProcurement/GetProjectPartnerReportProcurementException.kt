package io.cloudflight.jems.server.project.service.report.partner.procurement.getProjectPartnerReportProcurement

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX = "S-GPPRP"
private const val GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX = "use.case.get.project.partner.report.procurement"

class GetProjectPartnerReportProcurementException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class PartnerReportNotFound : ApplicationNotFoundException(
    code = "$GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.partner.report.not.found"),
)

class GetProjectPartnerReportProcurementsForSelectorException(cause: Throwable) : ApplicationException(
    code = "$GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX-FS",
    i18nMessage = I18nMessage("$GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.for.selector.failed"),
    cause = cause
)

class PartnerReportNotFoundForSelector : ApplicationNotFoundException(
    code = "$GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX-FS-001",
    i18nMessage = I18nMessage("$GET_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.for.selector.partner.report.not.found"),
)
