package io.cloudflight.jems.server.project.service.report.project.verification.file.delete

interface DeleteProjectReportVerificationFileInteractor {

    fun delete(projectId: Long, reportId: Long, fileId: Long)
}
