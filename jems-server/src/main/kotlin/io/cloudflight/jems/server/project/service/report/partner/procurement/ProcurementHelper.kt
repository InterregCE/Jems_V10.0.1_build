package io.cloudflight.jems.server.project.service.report.partner.procurement

import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementChange
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementSummary
import org.springframework.data.domain.Page
import java.math.BigDecimal

private val MAX_NUMBER = BigDecimal.valueOf(999_999_999_99, 2)
private val MIN_NUMBER = BigDecimal.ZERO

private const val MAX_AMOUNT_OF_PROCUREMENTS = 50L

fun Page<ProjectPartnerReportProcurementSummary>.fillThisReportFlag(currentReportId: Long) = map {
    it.apply {
        createdInThisReport = reportId == currentReportId
    }
}

fun ProjectPartnerReportProcurement.fillThisReportFlag(currentReportId: Long) = apply {
    createdInThisReport = reportId == currentReportId
}

fun ProjectPartnerReportStatusAndVersion.isClosed() = status.isClosed()

fun ProjectPartnerReportProcurementChange.getStaticValidationResults(validator: GeneralValidatorService) = listOf(
    validator.maxLength(contractName, 50, "contractName"),
    validator.maxLength(referenceNumber, 30, "referenceNumber"),
    validator.maxLength(contractType, 30, "contractType"),
    validator.maxLength(supplierName, 30, "supplierName"),
    validator.maxLength(vatNumber, 30, "vatNumber"),
    validator.maxLength(comment, 2000, "comment"),
    validator.numberBetween(contractAmount, MIN_NUMBER, MAX_NUMBER, "contractAmount"),
    validator.onlyValidCurrencies(currencyCodes = setOf(currencyCode).filterNotNullTo(HashSet()), "currencyCode"),
)

fun ProjectPartnerReportProcurementChange.validateAllowedCurrenciesIfEur(
    partnerCurrency: String?,
    exceptionResolver: (String) -> Exception,
) {
    if (partnerCurrency == "EUR" && currencyCode != partnerCurrency)
        throw exceptionResolver.invoke(currencyCode)
}

fun ProjectPartnerReportProcurementChange.validateContractNameIsUnique(
    currentProcurementId: Long,
    existingContractNames: Set<Pair<Long, String>>,
    exceptionResolver: (String) -> Exception,
) {
    val existingByName = existingContractNames.associateBy({ it.second }, { it.first })
    if (existingByName.contains(contractName) && existingByName[contractName] != currentProcurementId)
        throw exceptionResolver.invoke(contractName)
}

fun validateMaxAmountOfProcurements(amount: Long, exceptionResolver: (Int) -> Exception) {
    if (amount >= MAX_AMOUNT_OF_PROCUREMENTS)
        throw exceptionResolver.invoke(MAX_AMOUNT_OF_PROCUREMENTS.toInt())
}
