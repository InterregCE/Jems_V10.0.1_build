package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableLumpSumsForReport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_AVAILABLE_LUMP_SUMS_FOR_REPORT_ERROR_CODE_PREFIX = "S-GALFR"
private const val GET_AVAILABLE_LUMP_SUMS_FOR_REPORT_ERROR_KEY_PREFIX = "use.case.get.available.lumpSums.for.report"

class GetAvailableLumpSumsForReportException(cause: Throwable) : ApplicationException(
    code = GET_AVAILABLE_LUMP_SUMS_FOR_REPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_AVAILABLE_LUMP_SUMS_FOR_REPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
