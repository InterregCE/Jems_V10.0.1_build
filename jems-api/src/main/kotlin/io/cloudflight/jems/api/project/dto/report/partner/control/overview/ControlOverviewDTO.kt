package io.cloudflight.jems.api.project.dto.report.partner.control.overview

import java.time.LocalDate

data class ControlOverviewDTO(
    val startDate: LocalDate,
    val requestsForClarifications: String?,
    val receiptOfSatisfactoryAnswers: String?,
    val endDate: LocalDate?,
    val findingDescription: String?,
    val followUpMeasuresFromLastReport: String?,
    val conclusion: String?,
    val followUpMeasuresForNextReport: String?,
    val previousFollowUpMeasuresFromLastReport: String?,
    val changedLastCertifiedReportEndDate: LocalDate?,
    val lastCertifiedReportNumber: Int?
)
