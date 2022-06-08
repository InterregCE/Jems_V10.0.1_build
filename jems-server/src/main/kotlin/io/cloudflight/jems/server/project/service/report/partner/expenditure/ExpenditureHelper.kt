package io.cloudflight.jems.server.project.service.report.partner.expenditure

import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.model.expenditure.ReportBudgetCategory
import java.math.BigDecimal
import java.math.RoundingMode

fun List<ProjectPartnerReportExpenditureCost>.fillCurrencyRates(rates: Map<String, CurrencyConversion>) = map {
    it.apply {
        if (it.currencyCode !in rates.keys) {
            clearConversions()
        } else {
            fillInRate(rate = rates[it.currencyCode]!!.conversionRate)
        }
    }
}

fun List<ProjectPartnerReportExpenditureCost>.clearConversions() = map {
    it.apply { clearConversions() }
}

inline fun <T> Collection<T>.filterInvalidCurrencies(defaultCurrency: String?, extractFunction: (T) -> String) =
    if (defaultCurrency == "EUR")
        map { extractFunction.invoke(it) }.filterTo(HashSet()) { it != defaultCurrency }
    else
        emptySet()

fun ProjectPartnerReportExpenditureCost.fillInLumpSum(lumpSum: ProjectPartnerReportLumpSum) {
    unitCostId = null
    lumpSumId = lumpSum.id
    costCategory = ReportBudgetCategory.Multiple
    investmentId = null
    contractId = null
    internalReferenceNumber = null
    invoiceNumber = null
    invoiceDate = null
    dateOfPayment = null
    totalValueInvoice = null
    vat = null
    numberOfUnits = BigDecimal.ONE
    pricePerUnit = lumpSum.cost
    declaredAmount = lumpSum.cost
    currencyCode = "EUR"
}

fun ProjectPartnerReportExpenditureCost.fillInUnitCost(unitCost: ProjectPartnerReportUnitCost, currencyCodeValue: String) {
    unitCostId = unitCost.id
    lumpSumId = null
    costCategory = unitCost.category
    investmentId = null
    contractId = null
    internalReferenceNumber = null
    invoiceNumber = null
    invoiceDate = null
    dateOfPayment = null
    totalValueInvoice = null
    vat = null
    currencyCode = if (currencyCodeValue == "EUR" || currencyCodeValue == unitCost.foreignCurrencyCode) currencyCodeValue else "EUR"
    pricePerUnit = if (currencyCode == "EUR") unitCost.costPerUnit else unitCost.costPerUnitForeignCurrency!!
    declaredAmount = numberOfUnits.multiply(pricePerUnit).setScale(2, RoundingMode.DOWN)
}
