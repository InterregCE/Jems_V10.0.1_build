package io.cloudflight.ems.api.dto

enum class ProjectApplicationStatus {
    DRAFT,
    SUBMITTED,
    RETURNED_TO_APPLICANT,
    ELIGIBLE,
    INELIGIBLE,
    APPROVED,
    APPROVED_WITH_CONDITIONS,
    NOT_APPROVED;

    companion object {

        fun isNotSubmittedNow(status: ProjectApplicationStatus): Boolean {
            return status == DRAFT || status == RETURNED_TO_APPLICANT
        }

        fun isNotFinallyFunded(status: ProjectApplicationStatus): Boolean {
            return status != APPROVED && status != NOT_APPROVED && status != INELIGIBLE
        }

        fun wasSubmittedAtLeastOnce(status: ProjectApplicationStatus): Boolean {
            return status != DRAFT
        }

        fun isFundingStatus(status: ProjectApplicationStatus): Boolean {
            return listOf(APPROVED, APPROVED_WITH_CONDITIONS, NOT_APPROVED).contains(status)
        }

    }
}
