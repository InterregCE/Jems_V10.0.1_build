package io.cloudflight.jems.server.currency.service.importCurrency

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.currency.CurrencyDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.currency.entity.EuroExchangeRate
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.time.ZonedDateTime

class ImportCurrencyTest : UnitTest() {

    companion object {
        private val currentDate = ZonedDateTime.now()
        private val year = currentDate.year
        private val month = currentDate.month.value

        private val modelEur = CurrencyConversion("EUR", year, month, "Euro", BigDecimal.ONE)
        private val modelUsd = CurrencyConversion("USD", year, month, "US Dollar", BigDecimal(1.23))

        private val currencyEur = CurrencyDTO("EUR", year, month, "Euro", BigDecimal.ONE)
        private val currencyUsd = CurrencyDTO("USD", year, month, "US Dollar", BigDecimal(1.23))

        private val exEur = EuroExchangeRate("Austria", "Euro", "EUR", "AT", "1", "comment")
        private val exUsd = EuroExchangeRate("United States", "US Dollar", "USD", "US", "1.23", "")

        val rateBe = EuroExchangeRate(
            country = "Belgium",
            currency = "Euro",
            isoA3Code = "EUR",
            isoA2Code = "BE",
            value = "1",
            comment = "comment"
        )
        val rateBg = EuroExchangeRate(
            country = "Bulgaria",
            currency = "Bulgarian lev",
            isoA3Code = "BGN",
            isoA2Code = "BE",
            value = "1.9558",
            comment = null
        )
    }

    @MockK
    lateinit var persistence: CurrencyPersistence

    @RelaxedMockK
    lateinit var auditService: AuditService

    @MockK
    lateinit var restTemplate: RestTemplate
    @MockK
    lateinit var restTemplateBuilder: RestTemplateBuilder

    lateinit var importCurrency: ImportCurrency

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        every { restTemplateBuilder.build() } returns restTemplate
        importCurrency = ImportCurrency(
            restTemplateBuilder,
            persistence,
            auditService
        )
    }

    @Test
    fun `importCurrencyRates current - OK`() {
        val url = "https://ec.europa.eu/budg/inforeuro/api/public/monthly-rates?year=$year&month=$month&lang=en"
        every { restTemplate.getForObject(url, Array<EuroExchangeRate>::class.java) } returns arrayOf(exEur, exUsd)
        every { persistence.saveAll(any()) } returns listOf(modelEur, modelUsd)

        assertThat(importCurrency.importCurrencyRates(null, null)).contains(currencyEur, currencyUsd)

        val eventImportStart = slot<AuditCandidate>()
        val eventImportEnd = slot<AuditCandidate>()
        verifyOrder {
            auditService.logEvent(capture(eventImportStart))
            auditService.logEvent(capture(eventImportEnd))
        }
        assertThat(eventImportStart.captured.action).isEqualTo(AuditAction.CURRENCY_IMPORT)
        assertThat(eventImportStart.captured.description)
            .isEqualTo("There was an attempt to import Currency conversions. Import is starting...")
        assertThat(eventImportEnd.captured.action).isEqualTo(AuditAction.CURRENCY_IMPORT)
        assertThat(eventImportEnd.captured.description)
            .isEqualTo("'2' exchange rates have been successfully imported for ${year}, ${month}.")
    }

    @Test
    fun extractCurrencyRatesFromJson() {
        val url = "url"
        every { restTemplate.getForObject(url, Array<EuroExchangeRate>::class.java) } returns arrayOf(rateBe, rateBg)

        val currencies = restTemplate.getForObject(url, Array<EuroExchangeRate>::class.java)
        assertThat(currencies).isNotNull
        currencies?.contains(
            EuroExchangeRate(
                country = "Denmark",
                currency = "Danish krone",
                isoA3Code = "DKK",
                isoA2Code = "DK",
                value = "7.4365",
                comment = "Danish krone: Also in use for Faeroe Islands (FO), Greenland (GL)."
            )
        )
    }

}
