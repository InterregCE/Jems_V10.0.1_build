package io.cloudflight.jems.server.project.service.report.model.partner.control.expenditure

import java.time.ZonedDateTime

data class ParkExpenditureData (
    val expenditureId: Long,
    val originalReportId: Long,
    val originalNumber: Int,
    val parkedInProjectReportId: Long?,
    val parkedOn: ZonedDateTime
)
