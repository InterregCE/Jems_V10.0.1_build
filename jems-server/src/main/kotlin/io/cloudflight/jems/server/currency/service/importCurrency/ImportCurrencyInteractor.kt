package io.cloudflight.jems.server.currency.service.importCurrency

import io.cloudflight.jems.api.currency.CurrencyDTO

interface ImportCurrencyInteractor {

    fun importCurrencyRates(year: Int?, month: Int?): List<CurrencyDTO>

}
