package io.cloudflight.jems.server.currency.repository

import io.cloudflight.jems.server.currency.entity.CurrencyRate
import io.cloudflight.jems.server.currency.entity.CurrencyRateId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CurrencyRepository : JpaRepository<CurrencyRate, CurrencyRateId> {

    fun findAllByIdYearAndIdMonth(year: Int, month: Int): List<CurrencyRate>

    fun getByIdCodeAndIdYearAndIdMonth(code: String, year: Int, month: Int): CurrencyRate

}
