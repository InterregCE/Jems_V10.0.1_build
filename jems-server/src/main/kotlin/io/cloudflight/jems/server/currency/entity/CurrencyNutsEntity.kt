package io.cloudflight.jems.server.currency.entity

import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "currency_nuts")
class CurrencyNutsEntity (

    @EmbeddedId
    val id: CurrencyNutsIdEntity

)
