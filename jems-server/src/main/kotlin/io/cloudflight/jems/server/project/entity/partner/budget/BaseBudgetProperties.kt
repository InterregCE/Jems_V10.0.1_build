package io.cloudflight.jems.server.project.entity.partner.budget

import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
data class BaseBudgetProperties(
    @Column
    @field:NotNull
    val partnerId: Long,

    @Column
    @field:NotNull
    val numberOfUnits: BigDecimal,

    @Column
    @field:NotNull
    val pricePerUnit: BigDecimal,

    @Column
    @field:NotNull
    val rowSum: BigDecimal
)
