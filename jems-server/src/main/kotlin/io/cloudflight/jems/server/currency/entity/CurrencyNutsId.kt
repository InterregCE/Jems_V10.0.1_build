package io.cloudflight.jems.server.currency.entity

import java.io.Serializable
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
data class CurrencyNutsId(

    @field:NotNull
    val currencyCode: String,

    @field:NotNull
    val nutsId: String

): Serializable
