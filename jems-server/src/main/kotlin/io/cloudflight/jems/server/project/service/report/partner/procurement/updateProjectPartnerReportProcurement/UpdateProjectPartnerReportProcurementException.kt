package io.cloudflight.jems.server.project.service.report.partner.procurement.updateProjectPartnerReportProcurement

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UDPATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX = "S-UPPRP"
private const val UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX = "use.case.update.project.partner.report.procurement"

class UpdateProjectPartnerReportProcurementException(cause: Throwable) : ApplicationException(
    code = UDPATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class MaxAmountOfProcurementsReachedException(maxAmount: Int) : ApplicationUnprocessableException(
    code = "$UDPATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(
        "$UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.max.allowed.amount.reached",
        mapOf("maxSize" to maxAmount.toString())
    ),
    message = "max allowed: $maxAmount",
)

class ContractIdsAreNotUnique(notUniqueIds: Set<String>) : ApplicationUnprocessableException(
    code = "$UDPATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(
        "$UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.contractId.needs.to.be.unique",
        mapOf("contractId" to notUniqueIds.joinToString(", "))
    ),
    formErrors = notUniqueIds.associateBy({ it }, { I18nMessage(i18nKey = "not.unique") }),
    message = "duplicates: $notUniqueIds",
)

class InvalidCurrency(invalid: Set<String>) : ApplicationNotFoundException(
    code = "$UDPATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage(
        i18nKey = "$UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.invalid.currency",
        i18nArguments = mapOf(
            "invalid" to invalid.joinToString(", "),
        ),
    ),
)
