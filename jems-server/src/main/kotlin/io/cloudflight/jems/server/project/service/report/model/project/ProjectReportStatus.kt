package io.cloudflight.jems.server.project.service.report.model.project

enum class ProjectReportStatus {
    Draft,
    Submitted,
    InVerification,
    Finalized;

    fun isClosed() = SUBMITTED_STATUSES.contains(this)

    fun isOpen() = !isClosed()

    fun verificationNotStartedYet() = this !in VERIFICATION_STATUSES

    companion object {
        val SUBMITTED_STATUSES = setOf(Submitted, InVerification, Finalized)
        private val VERIFICATION_STATUSES = setOf(InVerification, Finalized)
    }

}
