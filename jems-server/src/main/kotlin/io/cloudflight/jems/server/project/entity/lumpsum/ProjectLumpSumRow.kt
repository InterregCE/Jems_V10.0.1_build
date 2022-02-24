package io.cloudflight.jems.server.project.entity.lumpsum

import java.math.BigDecimal

interface ProjectLumpSumRow {

    val projectId: Long
    val endPeriod: Int?
    val orderNr: Int
    val programmeLumpSumId: Long
    val projectPartnerId: Long?
    val amount: BigDecimal

}
