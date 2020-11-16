package io.cloudflight.jems.server.project.entity.partner.budget

import java.math.BigDecimal
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
data class Budget (

    @field:NotNull
    val numberOfUnits: BigDecimal,

    @field:NotNull
    val pricePerUnit: BigDecimal,

    @field:NotNull
    val rowSum: BigDecimal

)
