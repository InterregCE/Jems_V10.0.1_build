package io.cloudflight.jems.server.project.service.report.model.project

enum class ProjectReportStatus {
    Draft,
    Submitted,
    Verified,
    Paid;

    fun isClosed() = SUBMITTED_STATUSES.contains(this)

    fun isOpen() = !isClosed()

    companion object {
        private val SUBMITTED_STATUSES = setOf(Submitted, Verified, Paid)
    }

}
