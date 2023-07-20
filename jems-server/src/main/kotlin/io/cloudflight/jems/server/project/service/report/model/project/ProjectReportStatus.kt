package io.cloudflight.jems.server.project.service.report.model.project

enum class ProjectReportStatus {
    Draft,
    Submitted,
    InVerification,
    Finalized;

    fun isClosed() = SUBMITTED_STATUSES.contains(this)

    fun isOpen() = !isClosed()

    companion object {
        val SUBMITTED_STATUSES = setOf(Submitted, InVerification, Finalized)
    }

}
