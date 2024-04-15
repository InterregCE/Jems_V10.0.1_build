package io.cloudflight.jems.server.project.service.report.model.overview

interface ProjectReportLivingTableLine {
    fun retrieveOutputIndicatorId(): Long?
    fun retrieveResultIndicatorId(): Long?
}
