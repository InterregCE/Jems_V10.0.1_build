package io.cloudflight.jems.server.currency.entity

import java.io.Serializable
import java.util.Objects
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
class CurrencyNutsIdEntity(

    @field:NotNull
    val currencyCode: String,

    @field:NotNull
    val nutsId: String

): Serializable {
    override fun equals(other: Any?): Boolean = this === other ||
        other is CurrencyNutsIdEntity
        && currencyCode == other.currencyCode
        && nutsId == other.nutsId

    override fun hashCode(): Int = Objects.hash(currencyCode, nutsId)
}
