package io.cloudflight.jems.server.currency.repository

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.currency.entity.CurrencyNutsEntity
import io.cloudflight.jems.server.currency.entity.CurrencyNutsIdEntity
import io.cloudflight.jems.server.currency.entity.CurrencyRateEntity
import io.cloudflight.jems.server.currency.entity.CurrencyRateIdEntity
import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
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

        private val entityEur = CurrencyRateEntity(CurrencyRateIdEntity("EUR", 2022, 1), "Euro", BigDecimal.ONE)
        private val entityUsd = CurrencyRateEntity(CurrencyRateIdEntity("USD", 2022, 1), "US Dollar", BigDecimal(1.23))

        private val currencyNuts = CurrencyNutsEntity(CurrencyNutsIdEntity("EUR", "AT"))
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
        every { currencyRepository.getByIdCodeAndIdYearAndIdMonthOrderByIdCode(code, year, month) } returns entityEur

        assertThat(persistence.getConversionForCodeAndMonth(code, year, month)).isEqualTo(currencyEur)
    }

    @Test
    fun `save currencyRates`() {
        val entitiesSlot = slot<List<CurrencyRateEntity>>()
        every { currencyRepository.saveAll(capture(entitiesSlot)) } returns listOf(entityEur, entityUsd)

        assertThat(persistence.saveAll(listOf(currencyEur, currencyUsd)))
            .containsExactly(currencyEur, currencyUsd)

        with(entitiesSlot.captured[0]) {
            assertThat(id.code).isEqualTo(entityEur.id.code)
            assertThat(id.year).isEqualTo(entityEur.id.year)
            assertThat(id.month).isEqualTo(entityEur.id.month)
            assertThat(name).isEqualTo(entityEur.name)
            assertThat(conversionRate).isEqualTo(entityEur.conversionRate)
        }
        with(entitiesSlot.captured[1]) {
            assertThat(id.code).isEqualTo(entityUsd.id.code)
            assertThat(id.year).isEqualTo(entityUsd.id.year)
            assertThat(id.month).isEqualTo(entityUsd.id.month)
            assertThat(name).isEqualTo(entityUsd.name)
            assertThat(conversionRate).isEqualTo(entityUsd.conversionRate)
        }
    }

    @Test
    fun `get CurrencyRate for Country code`() {
        val countryCode = currencyNuts.id.nutsId
        every { currencyNutsRepository.getByIdNutsId(countryCode) } returns currencyNuts

        assertThat(persistence.getCurrencyForCountry(countryCode)).isEqualTo(currencyNuts.id.currencyCode)
    }

}
