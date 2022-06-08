package io.cloudflight.jems.server.currency.repository

import io.cloudflight.jems.server.currency.service.model.CurrencyConversion

interface CurrencyPersistence {

    fun findAllByIdYearAndIdMonth(year: Int, month: Int): List<CurrencyConversion>

    fun getConversionForCodeAndMonth(code: String, year: Int, month: Int): CurrencyConversion

    fun saveAll(currencies: List<CurrencyConversion>): List<CurrencyConversion>

    fun getCurrencyForCountry(country: String): String?
}
