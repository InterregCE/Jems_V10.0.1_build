package io.cloudflight.jems.server.project.service.report.model.partner.control.expenditure

import java.time.ZonedDateTime

data class ParkExpenditureData (
    val expenditureId: Long,
    val originalReportId: Long,
    val originalProjectReportId: Long?,
    val originalNumber: Int,
    val parkedOn: ZonedDateTime
)
