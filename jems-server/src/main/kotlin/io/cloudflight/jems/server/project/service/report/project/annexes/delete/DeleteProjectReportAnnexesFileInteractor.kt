package io.cloudflight.jems.server.project.service.report.project.annexes.delete

interface DeleteProjectReportAnnexesFileInteractor {

    fun delete(projectId: Long, reportId: Long, fileId: Long)
}
