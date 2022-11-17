package io.cloudflight.jems.server.project.entity.lumpsum

import java.math.BigDecimal
import java.sql.Timestamp

interface ProjectLumpSumRow {

    val projectId: Long
    val endPeriod: Int?
    val orderNr: Int
    val programmeLumpSumId: Long
    val projectPartnerId: Long?
    val amount: BigDecimal
    val fastTrack: Int?
    val readyForPayment: Int?
    val comment: String?
    val paymentEnabledDate: Timestamp?
    val lastApprovedVersionBeforeReadyForPayment: String?
}
