package io.cloudflight.jems.server.currency.controller

import io.cloudflight.jems.api.currency.CurrencyApi
import io.cloudflight.jems.api.currency.CurrencyDTO
import io.cloudflight.jems.server.currency.service.getCurrency.GetCurrencyInteractor
import io.cloudflight.jems.server.currency.service.importCurrency.ImportCurrencyInteractor
import org.springframework.web.bind.annotation.RestController
import java.util.Optional

@RestController
class CurrencyController(
    val getCurrencyInteractor: GetCurrencyInteractor,
    val importCurrencyInteractor: ImportCurrencyInteractor
): CurrencyApi {

    override fun getCurrencyRates(year: Optional<Int>, month: Optional<Int>): List<CurrencyDTO> {
        return getCurrencyInteractor.getCurrencyRates(year.orElse(null), month.orElse(null))
    }

    override fun fetchCurrencyRates(year: Optional<Int>, month: Optional<Int>): List<CurrencyDTO> {
        return importCurrencyInteractor.importCurrencyRates(year.orElse(null), month.orElse(null))
    }

}
