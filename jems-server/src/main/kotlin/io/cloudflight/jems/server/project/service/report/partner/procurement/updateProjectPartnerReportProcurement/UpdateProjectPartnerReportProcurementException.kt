package io.cloudflight.jems.server.project.service.report.partner.procurement.updateProjectPartnerReportProcurement

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX = "S-UPPRP"
private const val UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX = "use.case.update.project.partner.report.procurement"

class UpdateProjectPartnerReportProcurementException(cause: Throwable) : ApplicationException(
    code = UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ReportAlreadyClosed : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(i18nKey = "$UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.report.already.closed"),
)

class ContractNameIsNotUnique(notUniqueName: String) : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(
        "$UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.contractName.needs.to.be.unique",
        mapOf("contractName" to notUniqueName)
    ),
    formErrors = mapOf(notUniqueName to I18nMessage(i18nKey = "not.unique")),
    message = "duplicate name: $notUniqueName",
)

class InvalidCurrency(invalidCurrency: String) : ApplicationNotFoundException(
    code = "$UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage(
        i18nKey = "$UPDATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.invalid.currency",
        i18nArguments = mapOf(
            "invalid" to invalidCurrency,
        ),
    ),
)
