package io.cloudflight.jems.server.project.service.report.model.partner.control.overview

import java.time.LocalDate

data class ControlOverview (
    val startDate: LocalDate,
    val requestsForClarifications: String?,
    val receiptOfSatisfactoryAnswers: String?,
    var endDate: LocalDate?,
    val findingDescription: String?,
    val followUpMeasuresFromLastReport: String?,
    val conclusion: String?,
    val followUpMeasuresForNextReport: String?,
    var previousFollowUpMeasuresFromLastReport: String? = null,
    val lastCertifiedReportIdWhenCreation: Long? = null,
    var changedLastCertifiedReportEndDate: LocalDate? = null,
    var lastCertifiedReportNumber: Int? = null,
)
