package io.cloudflight.jems.server.currency.controller

import io.cloudflight.jems.api.currency.CurrencyDTO
import io.cloudflight.jems.server.currency.service.getCurrency.GetCurrencyInteractor
import io.cloudflight.jems.server.currency.service.importCurrency.ImportCurrencyInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.util.Optional

@ExtendWith(MockKExtension::class)
class CurrencyControllerTest {

    companion object {
        const val year = 2022
        const val month = 1

        private val currencyEur = CurrencyDTO("EUR", 2022, 1, "Euro", BigDecimal.ONE)
        private val currencyUsd = CurrencyDTO("USD", 2022, 1, "US Dollar", BigDecimal(1.23))
    }

    @MockK
    lateinit var getCurrencyInteractor: GetCurrencyInteractor

    @MockK
    lateinit var importCurrencyInteractor: ImportCurrencyInteractor

    @InjectMockKs
    private lateinit var controller: CurrencyController

    @Test
    fun `get CurrencyRates`() {
        every { getCurrencyInteractor.getCurrencyRates(year, month) } returns listOf(currencyEur, currencyUsd)
        assertThat(controller.getCurrencyRates(Optional.of(year), Optional.of(month)).size).isEqualTo(2)
    }

    @Test
    fun `get CurrencyRates current`() {
        every { getCurrencyInteractor.getCurrencyRates(null, null) } returns listOf(currencyEur)
        assertThat(controller.getCurrencyRates(Optional.empty(), Optional.empty())).contains(currencyEur)
    }

    @Test
    fun `fetch CurrencyRates`() {
        every { importCurrencyInteractor.importCurrencyRates(year, month) } returns listOf(currencyEur, currencyUsd)
        assertThat(controller.fetchCurrencyRates(Optional.of(year), Optional.of(month))).containsAll(
            listOf(currencyEur, currencyUsd)
        )
    }

    @Test
    fun `fetch CurrencyRates current`() {
        every { importCurrencyInteractor.importCurrencyRates(null, null) } returns emptyList()
        assertThat(controller.fetchCurrencyRates(Optional.empty(), Optional.empty())).isEmpty()
    }
}
