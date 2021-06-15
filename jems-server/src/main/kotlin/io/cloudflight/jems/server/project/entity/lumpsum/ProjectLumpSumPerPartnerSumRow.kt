package io.cloudflight.jems.server.project.entity.lumpsum

import java.math.BigDecimal

interface ProjectLumpSumPerPartnerSumRow {
    val partnerId: Long
    val sum: BigDecimal
}