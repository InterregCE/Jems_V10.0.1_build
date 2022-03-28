package io.cloudflight.jems.server.currency.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.currency.CurrencyDTO
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate

fun currencyImportRequest(): AuditCandidate {
    return AuditBuilder(AuditAction.CURRENCY_IMPORT)
        .description("There was an attempt to import Currency conversions. Import is starting...")
        .build()
}

fun currencyImportEnded(currencies: List<CurrencyDTO>): AuditCandidate {
    val year = currencies.firstOrNull()?.year
    val month = currencies.firstOrNull()?.month
    if (year == null || month == null) {
        return AuditBuilder(AuditAction.CURRENCY_IMPORT)
            .description("'${currencies.size}' exchange rates failed to be imported for ${year}, ${month}.")
            .build()
    }
    return AuditBuilder(AuditAction.CURRENCY_IMPORT)
        .description("'${currencies.size}' exchange rates have been successfully imported for ${year}, ${month}.")
        .build()
}
