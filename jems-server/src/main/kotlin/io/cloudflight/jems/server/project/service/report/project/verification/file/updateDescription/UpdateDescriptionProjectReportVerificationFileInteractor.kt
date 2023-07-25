package io.cloudflight.jems.server.project.service.report.project.verification.file.updateDescription

interface UpdateDescriptionProjectReportVerificationFileInteractor {

    fun updateDescription(projectId: Long, reportId: Long, fileId: Long, description: String)
}
