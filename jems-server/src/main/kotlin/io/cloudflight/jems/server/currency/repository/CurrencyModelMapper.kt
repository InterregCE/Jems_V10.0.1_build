package io.cloudflight.jems.server.currency.repository

import io.cloudflight.jems.server.currency.entity.CurrencyRate
import io.cloudflight.jems.server.currency.entity.CurrencyRateId
import io.cloudflight.jems.server.currency.entity.EuroExchangeRate
import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import java.math.BigDecimal

fun List<CurrencyRate>.toModelList() = map {
    it.toModel()
}

fun CurrencyRate.toModel() = CurrencyConversion(
    code = id.code,
    year = id.year,
    month = id.month,
    name = name,
    conversionRate = conversionRate
)

fun ArrayList<EuroExchangeRate>.toCurrencyConversions(year: Int, month: Int) = map {
    CurrencyConversion(
        code = it.isoA3Code,
        year = year,
        month = month,
        name = it.currency,
        conversionRate = BigDecimal(it.value)
    )
}

fun List<CurrencyConversion>.toEntities() = map {
    CurrencyRate(
        id = CurrencyRateId(it.code, it.year, it.month),
        name = it.name,
        conversionRate = it.conversionRate
    )
}
