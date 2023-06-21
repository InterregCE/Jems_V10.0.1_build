package io.cloudflight.jems.server.project.service.report.project.base.deleteProjectReport

interface DeleteProjectReportInteractor {

    fun delete(projectId: Long, reportId: Long)

}
