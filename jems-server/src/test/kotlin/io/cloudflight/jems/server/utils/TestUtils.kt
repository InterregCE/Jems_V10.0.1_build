package io.cloudflight.jems.server

import java.math.BigDecimal

fun Double.toScaledBigDecimal(): BigDecimal = BigDecimal.valueOf((BigDecimal.valueOf(this).multiply(BigDecimal.valueOf(100))).toLong(), 2)
fun Int.toScaledBigDecimal() = this.toDouble().toScaledBigDecimal()
