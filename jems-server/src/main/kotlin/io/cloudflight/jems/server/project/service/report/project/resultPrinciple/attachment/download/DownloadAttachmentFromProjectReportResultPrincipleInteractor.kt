package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.attachment.download

interface DownloadAttachmentFromProjectReportResultPrincipleInteractor {

    fun download(projectId: Long, reportId: Long, resultNumber: Int): Pair<String, ByteArray>
}
