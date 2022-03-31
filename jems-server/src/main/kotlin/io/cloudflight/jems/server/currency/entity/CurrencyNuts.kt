package io.cloudflight.jems.server.currency.entity

import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "currency_nuts")
data class CurrencyNuts (

    @EmbeddedId
    val id: CurrencyNutsId
)
