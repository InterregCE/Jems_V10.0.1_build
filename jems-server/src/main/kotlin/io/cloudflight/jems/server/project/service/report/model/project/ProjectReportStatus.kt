package io.cloudflight.jems.server.project.service.report.model.project

enum class ProjectReportStatus {
    Draft,
    Submitted,
    ReOpenSubmittedLast,
    ReOpenSubmittedLimited,
    InVerification,
    VerificationReOpenedLast,
    VerificationReOpenedLimited,
    Finalized,
    ReOpenFinalized;

    fun isClosed() = SUBMITTED_STATUSES.contains(this)
    fun hasBeenClosed() = !isOpenInitially()

    fun isOpenForNumbersChanges() = isOpenInitially() || this in UNLIMITED_REOPEN_STATUSES
    fun isOpenInitially() = this == Draft

    fun verificationNotStartedYet() = this !in VERIFICATION_STATUSES

    fun canNotBeReOpened() = this !in CAN_BE_OPENED_STATUSES

    fun isFinalized() = this == Finalized

    fun submitStatus(hasVerificationStartedBefore: Boolean) = when (this) {
        Draft, ReOpenSubmittedLast, ReOpenSubmittedLimited -> Submitted
        VerificationReOpenedLimited, VerificationReOpenedLast -> if (hasVerificationStartedBefore) ReOpenFinalized else InVerification
        else -> throw IllegalArgumentException("$this status is not submittable")
    }

    companion object {
        private val SUBMITTED_STATUSES = setOf(Submitted, InVerification, Finalized, ReOpenFinalized)
        val FINANCIALLY_CLOSED_STATUSES = SUBMITTED_STATUSES union setOf(ReOpenSubmittedLimited, VerificationReOpenedLimited)
        private val VERIFICATION_STATUSES = setOf(InVerification, VerificationReOpenedLast, VerificationReOpenedLimited, Finalized, ReOpenFinalized)
        private val CAN_BE_OPENED_STATUSES = setOf(Submitted, InVerification, ReOpenFinalized)
        val UNLIMITED_REOPEN_STATUSES = setOf(ReOpenSubmittedLast, VerificationReOpenedLast)
    }

}
