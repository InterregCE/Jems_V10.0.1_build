package io.cloudflight.jems.server.currency.entity

import java.io.Serializable
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
data class CurrencyRateId(

    @field:NotNull
    val code: String,

    @field:NotNull
    val year: Int,

    @field:NotNull
    val month: Int

): Serializable
