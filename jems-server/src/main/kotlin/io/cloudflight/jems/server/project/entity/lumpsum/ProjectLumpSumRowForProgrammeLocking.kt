package io.cloudflight.jems.server.project.entity.lumpsum

import java.math.BigDecimal

interface ProjectLumpSumRowForProgrammeLocking {
    val projectId: Long
    val endPeriod: Int?
    val orderNr: Int
    val programmeLumpSumId: Long
    val readyForPayment: Int?
    val comment: String?

}
