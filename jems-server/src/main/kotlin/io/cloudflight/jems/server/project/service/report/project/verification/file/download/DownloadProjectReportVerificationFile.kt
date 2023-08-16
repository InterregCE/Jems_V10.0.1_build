package io.cloudflight.jems.server.project.service.report.project.verification.file.download

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.authorization.CanViewReportVerificationCommunication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadProjectReportVerificationFile(
    private val filePersistence: JemsFilePersistence,
) : DownloadProjectReportVerificationFileInteractor {

    @CanViewReportVerificationCommunication
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadProjectReportVerificationFileException::class)
    override fun download(projectId: Long, reportId: Long, fileId: Long): Pair<String, ByteArray> {
        val filePath = JemsFileType.VerificationDocument.generatePath(projectId, reportId)

        return filePersistence.existsFile(exactPath = filePath, fileId = fileId)
            .let { exists -> if (exists) filePersistence.downloadFile(type = JemsFileType.VerificationDocument, fileId = fileId) else null }
            ?: throw FileNotFound()
    }
}
