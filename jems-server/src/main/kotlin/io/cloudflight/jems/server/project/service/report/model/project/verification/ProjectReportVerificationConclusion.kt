package io.cloudflight.jems.server.project.service.report.model.project.verification

import java.time.LocalDate

data class ProjectReportVerificationConclusion(
    val startDate: LocalDate?,
    val conclusionJS: String?,
    val conclusionMA: String?,
    val verificationFollowUp: String?
)
