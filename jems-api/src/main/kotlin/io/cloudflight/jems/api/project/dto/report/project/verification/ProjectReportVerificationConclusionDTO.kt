package io.cloudflight.jems.api.project.dto.report.project.verification

import java.time.LocalDate

data class ProjectReportVerificationConclusionDTO(
    val startDate: LocalDate?,
    val conclusionJS: String?,
    val conclusionMA: String?,
    val verificationFollowUp: String?
)