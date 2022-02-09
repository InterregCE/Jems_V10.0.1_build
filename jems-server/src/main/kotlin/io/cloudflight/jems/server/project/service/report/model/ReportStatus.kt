package io.cloudflight.jems.server.project.service.report.model

enum class ReportStatus {
    Draft,
    Submitted;

    fun isClosed() = this != Draft

}
