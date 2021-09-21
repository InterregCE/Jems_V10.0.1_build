package io.cloudflight.jems.server.project.service.application

enum class ApplicationStatus {
    STEP1_DRAFT,
    STEP1_SUBMITTED,
    STEP1_ELIGIBLE,
    STEP1_INELIGIBLE,
    STEP1_APPROVED,
    STEP1_APPROVED_WITH_CONDITIONS,
    STEP1_NOT_APPROVED,
    DRAFT,
    SUBMITTED,
    RETURNED_TO_APPLICANT,
    ELIGIBLE,
    INELIGIBLE,
    APPROVED,
    APPROVED_WITH_CONDITIONS,
    NOT_APPROVED;

    fun hasNotBeenSubmittedYet() = this == DRAFT || this == STEP1_DRAFT || this == RETURNED_TO_APPLICANT

    fun isSubmitted() = this == SUBMITTED || this == STEP1_SUBMITTED

    fun isDraftOrReturned() = hasNotBeenSubmittedYet()

    fun isInStep2() =
        this == DRAFT || this == SUBMITTED || this == RETURNED_TO_APPLICANT || this == ELIGIBLE || this == INELIGIBLE
            || this == APPROVED || this == APPROVED_WITH_CONDITIONS || this == NOT_APPROVED

    fun isEligible() = this == STEP1_ELIGIBLE || this == ELIGIBLE

}
