package io.cloudflight.jems.server.project.service.report.partner.procurement.createProjectPartnerReportProcurement

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val CREATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX = "S-CPPRP"
private const val CREATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX = "use.case.create.project.partner.report.procurement"

class CreateProjectPartnerReportProcurementException(cause: Throwable) : ApplicationException(
    code = CREATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CREATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ReportAlreadyClosed : ApplicationUnprocessableException(
    code = "$CREATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(i18nKey = "$CREATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.report.already.closed"),
)

class MaxAmountOfProcurementsReachedException(maxAmount: Int) : ApplicationUnprocessableException(
    code = "$CREATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(
        "$CREATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.max.allowed.amount.reached",
        mapOf("maxSize" to maxAmount.toString())
    ),
    message = "max allowed: $maxAmount",
)

class ContractNameIsNotUnique(notUniqueName: String) : ApplicationUnprocessableException(
    code = "$CREATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage(
        "$CREATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.contractName.needs.to.be.unique",
        mapOf("contractName" to notUniqueName)
    ),
    formErrors = mapOf(notUniqueName to I18nMessage(i18nKey = "not.unique")),
    message = "duplicate name: $notUniqueName",
)

class InvalidCurrency(invalidCurrency: String) : ApplicationNotFoundException(
    code = "$CREATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_CODE_PREFIX-004",
    i18nMessage = I18nMessage(
        i18nKey = "$CREATE_PROJECT_PARTNER_REPORT_PROCUREMENT_ERROR_KEY_PREFIX.invalid.currency",
        i18nArguments = mapOf(
            "invalid" to invalidCurrency,
        ),
    ),
)
