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
    APPROVED_WITH_CONDITIONS,
    NOT_APPROVED,
    APPROVED,

    MODIFICATION_PRECONTRACTING,
    MODIFICATION_PRECONTRACTING_SUBMITTED,

    CONTRACTED,

    IN_MODIFICATION,
    MODIFICATION_SUBMITTED,
    MODIFICATION_REJECTED;

    fun canBeModified() =
        isModifiableStatusBeforeApproved() || isModifiableStatusAfterApproved()

    fun isModifiableStatusBeforeApproved() =
        this == DRAFT || this == STEP1_DRAFT
            || this == RETURNED_TO_APPLICANT || this == RETURNED_TO_APPLICANT_FOR_CONDITIONS

    fun isModifiableStatusAfterApproved() =
        this == MODIFICATION_PRECONTRACTING || this == IN_MODIFICATION

    fun isApproved() = this == APPROVED || this == CONTRACTED

    fun isAlreadyApproved() = this == APPROVED || isAlreadyContracted()

    fun hasNotBeenApprovedYet() = !isAlreadyApproved()

    fun isAlreadyContracted() = setOf(
        CONTRACTED,
        IN_MODIFICATION,
        MODIFICATION_SUBMITTED,
        MODIFICATION_REJECTED,
    ).contains(this)

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

    fun isInStep1() = !isInStep2()

    fun isModificationSubmitted() = this == MODIFICATION_PRECONTRACTING_SUBMITTED || this == MODIFICATION_SUBMITTED

}
