package io.cloudflight.jems.server.currency.entity

import java.io.Serializable
import java.util.Objects
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
class CurrencyRateIdEntity(

    @field:NotNull
    val code: String,

    @field:NotNull
    val year: Int,

    @field:NotNull
    val month: Int

): Serializable {
    override fun equals(other: Any?): Boolean = this === other ||
        other is CurrencyRateIdEntity
        && code == other.code
        && year == other.year
        && month == other.month

    override fun hashCode(): Int = Objects.hash(code, year, month)
}

