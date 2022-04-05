package io.cloudflight.jems.server.currency.service.getCurrency

import io.cloudflight.jems.api.currency.CurrencyDTO
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.currency.service.toDto
import io.cloudflight.jems.server.currency.service.toDtoList
import io.cloudflight.jems.server.programme.authorization.CanRetrieveNuts
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class GetCurrency(
    private val persistence: CurrencyPersistence
) : GetCurrencyInteractor {

    @CanRetrieveNuts
    @Transactional(readOnly = true)
    override fun getCurrencyRates(year: Int?, month: Int?): List<CurrencyDTO> {
        val currentDate = ZonedDateTime.now()
        val loadYear = year ?: currentDate.year
        val loadMonth = month ?: currentDate.month.value

        return persistence.findAllByIdYearAndIdMonth(loadYear, loadMonth).toDtoList()
    }

    @CanRetrieveNuts
    @Transactional(readOnly = true)
    override fun getCurrencyRateForNutsRegion(country: String, year: Int?, month: Int?): CurrencyDTO? {
        val currentDate = ZonedDateTime.now()
        val loadYear = year ?: currentDate.year
        val loadMonth = month ?: currentDate.month.value

        val targetCurrency = persistence.getCurrencyForCountry(country)
        if (targetCurrency != null) {
            return persistence.getConversionForCodeAndMonth(targetCurrency, loadYear, loadMonth).toDto()
        }

        return null
    }

}
