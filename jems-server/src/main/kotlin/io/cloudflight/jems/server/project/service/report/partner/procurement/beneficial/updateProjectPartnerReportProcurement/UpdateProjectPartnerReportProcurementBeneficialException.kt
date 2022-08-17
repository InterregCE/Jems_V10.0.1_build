package io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.updateProjectPartnerReportProcurement

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_BENEFICIAL_ERROR_CODE_PREFIX = "S-UPPRPB"
private const val UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_BENEFICIAL_ERROR_KEY_PREFIX = "use.case.update.project.partner.report.procurement.beneficial"

class UpdateProjectPartnerReportProcurementBeneficialException(cause: Throwable) : ApplicationException(
    code = UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_BENEFICIAL_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_BENEFICIAL_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ReportNotFound(reportId: Long) : ApplicationNotFoundException(
    code = "$UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_BENEFICIAL_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(
        i18nKey = "$UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_BENEFICIAL_ERROR_KEY_PREFIX.report.not.found",
        i18nArguments = mapOf("reportId" to reportId.toString()),
    ),
)

class MaxAmountOfBeneficialReachedException(maxAmount: Int) : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_BENEFICIAL_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(i18nKey = "$UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_BENEFICIAL_ERROR_KEY_PREFIX.max.amount.of.beneficials.reached"),
    message = "max allowed: $maxAmount",
)

