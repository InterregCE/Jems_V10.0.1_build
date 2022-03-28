package io.cloudflight.jems.server.currency.entity

import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "currency_rate")
data class CurrencyRate (

    @EmbeddedId
    val id: CurrencyRateId,

    @field:NotNull
    val name: String,

    @Column(name = "conversion_rate")
    @field:NotNull
    val conversionRate: BigDecimal

)
