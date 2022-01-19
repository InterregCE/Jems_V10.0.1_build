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
    MODIFICATION_REJECTED,
    IN_MODIFICATION,
    MODIFICATION_SUBMITTED,
    NOT_APPROVED,
    CONTRACTED;

    fun canBeModified() =
        isModifiableStatusBeforeApproved() || isModifiableStatusAfterApproved()

    fun isModifiableStatusBeforeApproved() =
        this == DRAFT || this == STEP1_DRAFT
            || this == RETURNED_TO_APPLICANT || this == RETURNED_TO_APPLICANT_FOR_CONDITIONS

    fun isModifiableStatusAfterApproved() =
        this == MODIFICATION_PRECONTRACTING || this == IN_MODIFICATION

    fun isSubmitted() = this == SUBMITTED || this == STEP1_SUBMITTED

    fun isDraftOrReturned() = canBeModified()

    fun isInStep2() =
        this == DRAFT
            || this == RETURNED_TO_APPLICANT || this == RETURNED_TO_APPLICANT_FOR_CONDITIONS
            || this == ELIGIBLE || this == INELIGIBLE
            || this == APPROVED || this == APPROVED_WITH_CONDITIONS || this == NOT_APPROVED
            || this == SUBMITTED || this == CONDITIONS_SUBMITTED || this == MODIFICATION_PRECONTRACTING_SUBMITTED
            || this == MODIFICATION_PRECONTRACTING
            || this == MODIFICATION_REJECTED
            || this == CONTRACTED
            || this == IN_MODIFICATION
            || this == MODIFICATION_SUBMITTED

    fun isEligible() = this == STEP1_ELIGIBLE || this == ELIGIBLE

}
