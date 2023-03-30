package io.cloudflight.jems.server.project.service.report.partner.expenditure

import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import java.math.BigDecimal
import java.math.RoundingMode

fun List<ProjectPartnerReportExpenditureCost>.fillCurrencyRates(rates: Map<String, CurrencyConversion>) = map {
    it.apply {
        val rate: BigDecimal? = if (it.parkingMetadata != null) it.currencyConversionRate!! else rates.getOrDefault(it.currencyCode, null)?.conversionRate
        if (rate != null) {
            fillInRate(rate)
        } else {
            clearConversions()
        }
    }
}

fun List<ProjectPartnerReportExpenditureCost>.clearConversions(exceptReIncluded: Map<Long, ProjectPartnerReportExpenditureCost>) = map {
    it.apply {
        if (id in exceptReIncluded.keys) {
            currencyCode = exceptReIncluded[id]!!.currencyCode
            currencyConversionRate = exceptReIncluded[id]!!.currencyConversionRate
            declaredAmountAfterSubmission = null
        } else {
            clearConversions()
        }
    }
}

fun List<ProjectPartnerReportExpenditureCost>.clearParking() = map {
    it.apply { it.parkingMetadata = null }
}

fun List<ProjectPartnerReportExpenditureCost>.reNumber(ignoreIds: Set<Long>): List<ProjectPartnerReportExpenditureCost> {
    var skippedParked = 0
    this.forEachIndexed { index, expenditure ->
        if (expenditure.id in ignoreIds) {
            expenditure.number = 0
            skippedParked += 1
        } else {
            expenditure.number = index.plus(1).minus(skippedParked)
        }
    }
    return this
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
