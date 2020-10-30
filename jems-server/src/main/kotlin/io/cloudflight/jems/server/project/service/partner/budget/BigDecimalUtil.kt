package io.cloudflight.jems.server.project.service.partner.budget

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.truncate(): BigDecimal =
    setScale(2, RoundingMode.FLOOR)

fun BigDecimal.percentage(percentage: Int): BigDecimal =
    multiply(BigDecimal.valueOf(percentage.toLong()))
        .divide(BigDecimal(100))
        .truncate()
