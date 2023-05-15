package io.cloudflight.jems.server.project.service.report.model.partner

enum class ReportStatus {
    Draft,
    Submitted,
    ReOpenSubmittedLast,
    ReOpenSubmittedLimited,
    InControl,
    ReOpenInControlLast,
    ReOpenInControlLimited,
    Certified;

    fun isClosed() = SUBMITTED_STATUSES.contains(this)
    fun hasBeenClosed() = !isOpenInitially()

    fun isOpenForNumbersChanges() = isOpenInitially() || this in ARE_LAST_OPEN_STATUSES
    fun isOpenInitially() = this == Draft

    fun controlNotOpenAnymore() = this != InControl
    fun controlNotStartedYet() = this !in CONTROL_STATUSES

    fun canNotBeReOpened() = this !in CAN_BE_OPENED_STATUSES

    fun isFinalized() = this == Certified

    fun submitStatus() = when (this) {
        Draft, ReOpenSubmittedLast, ReOpenSubmittedLimited -> Submitted
        ReOpenInControlLast, ReOpenInControlLimited -> InControl
        else -> throw IllegalArgumentException("$this status is not submittable")
    }

    companion object {
        private val SUBMITTED_STATUSES = setOf(Submitted, InControl, Certified)
        val FINANCIALLY_CLOSED_STATUSES = SUBMITTED_STATUSES union setOf(ReOpenInControlLimited, ReOpenSubmittedLimited)
        private val CONTROL_STATUSES = setOf(InControl, ReOpenInControlLast, ReOpenInControlLimited, Certified)
        private val CAN_BE_OPENED_STATUSES = setOf(Submitted, InControl)
        val ARE_LAST_OPEN_STATUSES = setOf(ReOpenSubmittedLast, ReOpenInControlLast)
    }

}
