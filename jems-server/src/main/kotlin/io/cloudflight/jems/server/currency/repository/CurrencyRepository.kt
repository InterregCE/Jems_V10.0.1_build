package io.cloudflight.jems.server.currency.repository

import io.cloudflight.jems.server.currency.entity.CurrencyRateEntity
import io.cloudflight.jems.server.currency.entity.CurrencyRateIdEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CurrencyRepository : JpaRepository<CurrencyRateEntity, CurrencyRateIdEntity> {

    fun findAllByIdYearAndIdMonth(year: Int, month: Int): List<CurrencyRateEntity>

    fun getByIdCodeAndIdYearAndIdMonthOrderByIdCode(code: String, year: Int, month: Int): CurrencyRateEntity

}
