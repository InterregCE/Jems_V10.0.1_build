package io.cloudflight.jems.server.currency.entity

import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "currency_rate")
class CurrencyRateEntity (

    @EmbeddedId
    val id: CurrencyRateIdEntity,

    @field:NotNull
    val name: String,

    @Column(name = "conversion_rate")
    @field:NotNull
    val conversionRate: BigDecimal

)
