package io.cloudflight.jems.server.project.service.report.project.verification.certificate.download

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.VerificationCertificate
import io.cloudflight.jems.server.project.authorization.CanViewReportVerificationPrivileged
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadProjectReportVerificationCertificate(
    private val filePersistence: JemsFilePersistence,
) : DownloadProjectReportVerificationCertificateInteractor {

    @CanViewReportVerificationPrivileged
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadProjectReportVerificationCertificateException::class)
    override fun download(projectId: Long, reportId: Long, fileId: Long): Pair<String, ByteArray> {
        val filePath = VerificationCertificate.generatePath(projectId, reportId)

        return filePersistence.existsFile(exactPath = filePath, fileId = fileId)
            .let { exists -> if (exists) filePersistence.downloadFile(type = VerificationCertificate, fileId = fileId) else null }
            ?: throw FileNotFound()
    }
}
