package io.cloudflight.jems.server.project.service.report.project.annexes.update

interface SetDescriptionToProjectReportFileInteractor {

    fun update(projectId: Long, reportId: Long, fileId: Long, description: String)
}
