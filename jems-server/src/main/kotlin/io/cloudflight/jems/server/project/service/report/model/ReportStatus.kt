package io.cloudflight.jems.server.project.service.report.model

enum class ReportStatus {
    Draft,
    Submitted,
    InControl;

    fun isClosed() = SUBMITTED_STATUSES.contains(this)

    companion object {
        val SUBMITTED_STATUSES = setOf(Submitted, InControl)
    }

}
