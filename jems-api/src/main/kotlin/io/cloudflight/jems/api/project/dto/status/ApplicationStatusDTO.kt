package io.cloudflight.jems.api.project.dto.status

enum class ApplicationStatusDTO {
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

    companion object {

        fun isNotSubmittedNow(status: ApplicationStatusDTO): Boolean {
            return status == DRAFT || status == STEP1_DRAFT || status == RETURNED_TO_APPLICANT
        }

        fun isNotFinallyFunded(status: ApplicationStatusDTO): Boolean {
            return status != APPROVED && status != NOT_APPROVED && status != INELIGIBLE &&
                    status != STEP1_APPROVED && status != STEP1_NOT_APPROVED && status != STEP1_INELIGIBLE
        }

        fun wasSubmittedAtLeastOnce(status: ApplicationStatusDTO): Boolean {
            return status != DRAFT && status != STEP1_DRAFT
        }

        fun isDraft(status: ApplicationStatusDTO): Boolean {
            return status == DRAFT || status == STEP1_DRAFT
        }

        fun isSubmitted(status: ApplicationStatusDTO): Boolean {
            return status == SUBMITTED || status == STEP1_SUBMITTED
        }

        fun isEligible(status: ApplicationStatusDTO): Boolean {
            return status == ELIGIBLE || status == STEP1_ELIGIBLE
        }
    }
}
