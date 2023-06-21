package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.attachment.delete

interface DeleteAttachmentFromProjectReportResultPrincipleInteractor {

    fun delete(projectId: Long, reportId: Long, resultNumber: Int)
}
