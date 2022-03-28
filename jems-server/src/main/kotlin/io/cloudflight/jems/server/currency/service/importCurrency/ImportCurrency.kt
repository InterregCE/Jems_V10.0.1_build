package io.cloudflight.jems.server.currency.service.importCurrency

import io.cloudflight.jems.api.currency.CurrencyDTO
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.currency.entity.EuroExchangeRate
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.currency.service.currencyImportEnded
import io.cloudflight.jems.server.currency.service.currencyImportRequest
import io.cloudflight.jems.server.currency.service.toDtoList
import io.cloudflight.jems.server.currency.service.toModelList
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.time.ZonedDateTime

@Service
class ImportCurrency(
    restTemplateBuilder: RestTemplateBuilder,
    private val persistence: CurrencyPersistence,
    private val auditService: AuditService
) : ImportCurrencyInteractor {

   companion object {
        private val log = LoggerFactory.getLogger(this::class.java)

        const val EC_EUROPA_CONVERSION_URL = "https://ec.europa.eu/budg/inforeuro/api/public/monthly-rates"
        const val EC_EUROPA_APPENDER_URL = "&lang=en"
    }

    val restTemplate: RestTemplate = restTemplateBuilder.build()

    @Transactional
    @CanUpdateProgrammeSetup
    override fun importCurrencyRates(year: Int?, month: Int?): List<CurrencyDTO> {
        val currentDate = ZonedDateTime.now()
        val loadYear = year ?: currentDate.year
        val loadMonth = month ?: currentDate.month.value
        val url = "$EC_EUROPA_CONVERSION_URL?year=$loadYear&month=$loadMonth$EC_EUROPA_APPENDER_URL"

        auditService.logEvent(currencyImportRequest())
        log.info("Currency exchange rates download from $url..")
        val dataToImport = extractCurrencyRates(url = url)
        log.info("Currency exchange rates downloaded from $url, size: ${dataToImport.size}")

        val currencies = persistence.saveAll(
            dataToImport.toModelList(loadYear, loadMonth)
        ).toDtoList()
        auditService.logEvent(currencyImportEnded(currencies))

        return currencies
    }

    private fun extractCurrencyRates(url: String): ArrayList<EuroExchangeRate> {
        val exchangeRates = restTemplate.getForObject(url, Array<EuroExchangeRate>::class.java) ?: return arrayListOf()
        return ArrayList(listOf(*exchangeRates))
    }

}
