package io.cloudflight.jems.server.project.service.report.partner.expenditure

import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost

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
