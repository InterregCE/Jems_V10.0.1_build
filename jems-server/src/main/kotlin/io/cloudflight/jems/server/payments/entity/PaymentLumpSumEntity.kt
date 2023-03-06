package io.cloudflight.jems.server.payments.entity

import java.io.Serializable
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
class PaymentLumpSumEntity (

    @field:NotNull
    val programmeLumpSumId: Long,

    @field:NotNull
    val orderNr: Int,

): Serializable
