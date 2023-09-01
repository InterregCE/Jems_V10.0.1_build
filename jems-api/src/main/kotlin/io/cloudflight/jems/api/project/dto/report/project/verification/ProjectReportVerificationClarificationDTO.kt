package io.cloudflight.jems.api.project.dto.report.project.verification

import java.time.LocalDate

data class ProjectReportVerificationClarificationDTO(
    val id: Long,
    val number: Int,
    val requestDate: LocalDate,
    val answerDate: LocalDate?,
    val comment: String
)
