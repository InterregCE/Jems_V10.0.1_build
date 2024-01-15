package io.cloudflight.jems.api.project.dto.report.project

enum class ProjectReportStatusDTO {
    Draft,
    Submitted,
    ReOpenSubmittedLast,
    ReOpenSubmittedLimited,
    InVerification,
    VerificationReOpenedLast,
    VerificationReOpenedLimited,
    Finalized,
    ReOpenFinalized,
}
