package io.cloudflight.jems.server.project.service.report.project.verification.file.download

interface DownloadProjectReportVerificationFileInteractor {

    fun download(projectId: Long, reportId: Long, fileId: Long): Pair<String, ByteArray>
}
