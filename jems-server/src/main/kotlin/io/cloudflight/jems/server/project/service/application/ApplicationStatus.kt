package io.cloudflight.jems.server.project.service.application

enum class ApplicationStatus {
    DRAFT,
    SUBMITTED,
    RETURNED_TO_APPLICANT,
    ELIGIBLE,
    INELIGIBLE,
    APPROVED,
    APPROVED_WITH_CONDITIONS,
    NOT_APPROVED;
}
