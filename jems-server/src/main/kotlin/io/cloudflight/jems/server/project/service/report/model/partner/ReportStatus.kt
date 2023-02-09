package io.cloudflight.jems.server.project.service.report.model.partner

enum class ReportStatus {
    Draft,
    Submitted,
    InControl,
    Certified;

    fun isClosed() = SUBMITTED_STATUSES.contains(this)

    fun isOpen() = !isClosed()

    fun controlNotOpenAnymore() = this != InControl
    fun controlNotStartedYet() = this !in CONTROL_STATUSES

    fun isFinalized() = this == Certified

    companion object {
        val SUBMITTED_STATUSES = setOf(Submitted, InControl, Certified)
        val CONTROL_STATUSES = setOf(InControl, Certified)
    }

}
