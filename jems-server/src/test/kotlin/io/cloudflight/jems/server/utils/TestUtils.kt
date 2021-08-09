package io.cloudflight.jems.server

import java.math.BigDecimal

fun Double.toScaledBigDecimal(): BigDecimal = BigDecimal.valueOf((this * 100).toLong(), 2)
fun Int.toScaledBigDecimal() = this.toDouble().toScaledBigDecimal()
