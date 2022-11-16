package io.cloudflight.jems.server.project.service.report.model.partner

enum class ReportStatus {
    Draft,
    Submitted,
    InControl,
    Certified;

    fun isClosed() = SUBMITTED_STATUSES.contains(this)

    fun isOpen() = !isClosed()

    companion object {
        val SUBMITTED_STATUSES = setOf(Submitted, InControl, Certified)
    }

}
