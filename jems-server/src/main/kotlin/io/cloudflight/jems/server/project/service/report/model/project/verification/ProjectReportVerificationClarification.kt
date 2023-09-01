package io.cloudflight.jems.server.project.service.report.model.project.verification

import java.time.LocalDate

data class ProjectReportVerificationClarification(
    val id: Long = 0L,
    var number: Int,
    val requestDate: LocalDate,
    val answerDate: LocalDate?,
    val comment: String,
)
