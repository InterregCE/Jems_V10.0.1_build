package io.cloudflight.jems.server.currency.repository

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.currency.entity.CurrencyNuts
import io.cloudflight.jems.server.currency.entity.CurrencyNutsId
import io.cloudflight.jems.server.currency.entity.CurrencyRate
import io.cloudflight.jems.server.currency.entity.CurrencyRateId
import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CurrencyPersistenceTest: UnitTest() {

    companion object {
        const val year = 2022
        const val month = 1

        private val currencyEur = CurrencyConversion("EUR", 2022, 1, "Euro", BigDecimal.ONE)
        private val currencyUsd = CurrencyConversion("USD", 2022, 1, "US Dollar", BigDecimal(1.23))

        private val entityEur = CurrencyRate(CurrencyRateId("EUR", 2022, 1), "Euro", BigDecimal.ONE)
        private val entityUsd = CurrencyRate(CurrencyRateId("USD", 2022, 1), "US Dollar", BigDecimal(1.23))

        private val currencyNuts = CurrencyNuts(CurrencyNutsId("EUR", "AT"))
    }

    @MockK
    lateinit var currencyRepository: CurrencyRepository

    @MockK
    lateinit var currencyNutsRepository: CurrencyNutsRepository

    @InjectMockKs
    private lateinit var persistence: CurrencyPersistenceProvider

    @Test
    fun `findAll by Year and Month`() {
        every { currencyRepository.findAllByIdYearAndIdMonth(year, month) } returns listOf(entityEur, entityUsd)

        assertThat(persistence.findAllByIdYearAndIdMonth(year, month)).containsExactly(currencyEur, currencyUsd)
        verify { currencyRepository.findAllByIdYearAndIdMonth(year, month) }
    }

    @Test
    fun `get by Year and Month`() {
        val code = entityEur.id.code
        every { currencyRepository.getByIdCodeAndIdYearAndIdMonth(code, year, month) } returns entityEur

        assertThat(persistence.getByIdCodeAndIdYearAndIdMonth(code, year, month)).isEqualTo(currencyEur)
    }

    @Test
    fun `save currencyRates`() {
        every { currencyRepository.saveAll(listOf(entityEur, entityUsd)) } returns listOf(entityEur, entityUsd)

        assertThat(persistence.saveAll(listOf(currencyEur, currencyUsd)))
            .containsExactly(currencyEur, currencyUsd)
    }

    @Test
    fun `get CurrencyRate for Country code`() {
        val countryCode = currencyNuts.id.nutsId
        every { currencyNutsRepository.getByIdNutsId(countryCode) } returns currencyNuts

        assertThat(persistence.getCurrencyForCountry(countryCode)).isEqualTo(currencyNuts.id.currencyCode)
    }

}
