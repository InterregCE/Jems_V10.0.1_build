package io.cloudflight.jems.server.project.entity.partner.budget

import java.math.BigDecimal

interface Budget {
    val id: Long?
    val partnerId: Long
    val numberOfUnits: BigDecimal
    val pricePerUnit: BigDecimal
}
