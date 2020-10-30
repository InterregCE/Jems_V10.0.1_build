package io.cloudflight.jems.server.project.entity.partner.budget

import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class Budget (

    @Column
    val description: String? = null,

    @Column(nullable = false)
    val numberOfUnits: BigDecimal,

    @Column(nullable = false)
    val pricePerUnit: BigDecimal,

    @Column(nullable = false)
    val rowSum: BigDecimal

)
