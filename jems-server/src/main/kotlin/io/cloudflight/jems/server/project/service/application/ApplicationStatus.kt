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
    CONDITIONS_SUBMITTED,
    RETURNED_TO_APPLICANT,
    RETURNED_TO_APPLICANT_FOR_CONDITIONS,
    ELIGIBLE,
    INELIGIBLE,
    APPROVED,
    APPROVED_WITH_CONDITIONS,
    MODIFICATION_PRECONTRACTING,
    MODIFICATION_PRECONTRACTING_SUBMITTED,
    NOT_APPROVED;

    fun canBeModified() = this == DRAFT || this == STEP1_DRAFT || this == RETURNED_TO_APPLICANT || this == RETURNED_TO_APPLICANT_FOR_CONDITIONS

    fun isSubmitted() = this == SUBMITTED || this == STEP1_SUBMITTED

    fun isDraftOrReturned() = canBeModified()

    fun isInStep2() =
        this == DRAFT || this == SUBMITTED || this == CONDITIONS_SUBMITTED || this == RETURNED_TO_APPLICANT || this == ELIGIBLE || this == INELIGIBLE
            || this == APPROVED || this == APPROVED_WITH_CONDITIONS || this == NOT_APPROVED || this == RETURNED_TO_APPLICANT_FOR_CONDITIONS

    fun isEligible() = this == STEP1_ELIGIBLE || this == ELIGIBLE

}
