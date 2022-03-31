package io.cloudflight.jems.server.currency.repository

import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class CurrencyPersistenceProvider(
    private val currencyRepository: CurrencyRepository,
    private val currencyNutsRepository: CurrencyNutsRepository
) : CurrencyPersistence {

    @Transactional(readOnly = true)
    override fun findAllByIdYearAndIdMonth(year: Int, month: Int): List<CurrencyConversion> {
        return currencyRepository.findAllByIdYearAndIdMonth(year, month).toModelList()
    }

    @Transactional(readOnly = true)
    override fun getByIdCodeAndIdYearAndIdMonth(code: String, year: Int, month: Int): CurrencyConversion {
        return currencyRepository.getByIdCodeAndIdYearAndIdMonth(code, year, month).toModel()
    }

    @Transactional
    override fun saveAll(currencies: List<CurrencyConversion>): List<CurrencyConversion> {
        return currencyRepository.saveAll(currencies.toEntities()).toModelList()
    }

    @Transactional(readOnly = true)
    override fun getCurrencyForCountry(country: String): String? {
        return currencyNutsRepository.getByIdNutsId(country)?.id?.currencyCode
    }
}
