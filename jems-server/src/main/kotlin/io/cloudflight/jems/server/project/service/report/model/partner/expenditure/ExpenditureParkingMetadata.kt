package io.cloudflight.jems.server.project.service.report.model.partner.expenditure

import java.time.ZonedDateTime

data class ExpenditureParkingMetadata(
    val reportOfOriginId: Long,
    val reportOfOriginNumber: Int,
    val reportProjectOfOriginId: Long?,
    val originalExpenditureNumber: Int,
    val parkedFromExpenditureId: Long,
    val parkedOn: ZonedDateTime?,
)
