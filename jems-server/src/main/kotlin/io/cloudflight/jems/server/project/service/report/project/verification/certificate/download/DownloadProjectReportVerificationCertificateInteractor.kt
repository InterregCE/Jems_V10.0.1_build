package io.cloudflight.jems.server.project.service.report.project.verification.certificate.download

interface DownloadProjectReportVerificationCertificateInteractor {

    fun download(projectId: Long, reportId: Long, fileId: Long): Pair<String, ByteArray>
}
