package io.cloudflight.jems.server.currency.service

import io.cloudflight.jems.api.currency.CurrencyDTO
import io.cloudflight.jems.server.currency.service.model.EuroExchangeRate
import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import java.math.BigDecimal

fun List<CurrencyConversion>.toDtoList() = map {
    it.toDto()
}

fun CurrencyConversion.toDto() = CurrencyDTO(
    code = code,
    year = year,
    month = month,
    name = name,
    conversionRate = conversionRate
)

fun List<EuroExchangeRate>.toModelList(year: Int, month: Int) = map {
    CurrencyConversion(
        code = it.isoA3Code,
        year = year,
        month = month,
        name = it.currency,
        conversionRate = BigDecimal(it.value)
    )
}
