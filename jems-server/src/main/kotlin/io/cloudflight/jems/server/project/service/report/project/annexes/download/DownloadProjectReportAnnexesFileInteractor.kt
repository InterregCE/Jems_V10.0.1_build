package io.cloudflight.jems.server.project.service.report.project.annexes.download

interface DownloadProjectReportAnnexesFileInteractor {

    fun download(projectId: Long, reportId: Long, fileId: Long): Pair<String, ByteArray>
}
