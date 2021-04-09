package io.cloudflight.jems.api.project.dto.status

enum class ApplicationStatusDTO {
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
            return status == DRAFT || status == RETURNED_TO_APPLICANT
        }

        fun isNotFinallyFunded(status: ApplicationStatusDTO): Boolean {
            return status != APPROVED && status != NOT_APPROVED && status != INELIGIBLE
        }

        fun wasSubmittedAtLeastOnce(status: ApplicationStatusDTO): Boolean {
            return status != DRAFT
        }

        fun isFundingStatus(status: ApplicationStatusDTO): Boolean {
            return listOf(APPROVED, APPROVED_WITH_CONDITIONS, NOT_APPROVED).contains(status)
        }

    }
}
