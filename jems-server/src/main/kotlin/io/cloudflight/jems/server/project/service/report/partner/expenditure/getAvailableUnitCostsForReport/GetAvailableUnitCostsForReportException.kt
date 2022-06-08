package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableUnitCostsForReport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_AVAILABLE_UNIT_COSTS_FOR_REPORT_ERROR_CODE_PREFIX = "S-GAUFR"
private const val GET_AVAILABLE_UNIT_COSTS_FOR_REPORT_ERROR_KEY_PREFIX = "use.case.get.available.unitCosts.for.report"


class GetAvailableUnitCostsForReportException(cause: Throwable) : ApplicationException(
    code = GET_AVAILABLE_UNIT_COSTS_FOR_REPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_AVAILABLE_UNIT_COSTS_FOR_REPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
