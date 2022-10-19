package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureUnitCostBreakdown

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_REPORT_UNIT_COST_BREAKDOWN_ERROR_CODE_PREFIX = "S-GRUB"
private const val GET_REPORT_UNIT_COST_BREAKDOWN_ERROR_KEY_PREFIX = "use.case.get.report.unitCost.breakdown"

class GetReportExpenditureUnitCostBreakdownException(cause: Throwable) : ApplicationException(
    code = GET_REPORT_UNIT_COST_BREAKDOWN_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_REPORT_UNIT_COST_BREAKDOWN_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
