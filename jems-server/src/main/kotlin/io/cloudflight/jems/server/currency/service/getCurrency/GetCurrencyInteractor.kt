package io.cloudflight.jems.server.currency.service.getCurrency

import io.cloudflight.jems.api.currency.CurrencyDTO

interface GetCurrencyInteractor {

    fun getCurrencyRates(year: Int?, month: Int?): List<CurrencyDTO>

    fun getCurrencyRateForNutsRegion(country: String, year: Int?, month: Int?): CurrencyDTO?

}
