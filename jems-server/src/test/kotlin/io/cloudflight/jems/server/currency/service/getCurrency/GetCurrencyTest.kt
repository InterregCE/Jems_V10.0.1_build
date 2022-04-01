package io.cloudflight.jems.server.currency.service.getCurrency

import io.cloudflight.jems.api.currency.CurrencyDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.currency.entity.CurrencyNuts
import io.cloudflight.jems.server.currency.entity.CurrencyNutsId
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetCurrencyTest : UnitTest() {

    companion object {
        const val year = 2022
        const val month = 1
        private val currencyNuts = CurrencyNuts(CurrencyNutsId("EUR", "AT"))

        private val currencyEur = CurrencyDTO("EUR", 2022, 1, "Euro", BigDecimal.ONE)
        private val currencyUsd = CurrencyDTO("USD", 2022, 1, "US Dollar", BigDecimal(1.23))

        private val modelEur = CurrencyConversion("EUR", 2022, 1, "Euro", BigDecimal.ONE)
        private val modelUsd = CurrencyConversion("USD", 2022, 1, "US Dollar", BigDecimal(1.23))
    }

    @MockK
    lateinit var persistence: CurrencyPersistence

    @InjectMockKs
    private lateinit var getCurrency: GetCurrency

    @Test
    fun `getCurrencyRates - OK`() {
        every { persistence.findAllByIdYearAndIdMonth(year, month) } returns listOf(modelEur, modelUsd)

        assertThat(getCurrency.getCurrencyRates(year, month)).contains(currencyEur, currencyUsd)
    }

    @Test
    fun `getCurrencyRateForNutsRegion - OK`() {
        val countryCode = currencyNuts.id.nutsId
        val currencyCode = currencyNuts.id.currencyCode
        every { persistence.getCurrencyForCountry(countryCode) } returns currencyCode
        every { persistence.getByIdCodeAndIdYearAndIdMonth(currencyCode, year, month) } returns modelEur

        assertThat(getCurrency.getCurrencyRateForNutsRegion(countryCode, year, month)).isEqualTo(currencyEur)
    }

}
