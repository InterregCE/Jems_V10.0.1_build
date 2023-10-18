package io.cloudflight.jems.server.project.service.auditAndControl.file.download

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.authorization.CanViewProjectAuditAndControl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadAuditControlFile(
    private val filePersistence: JemsFilePersistence,
): DownloadAuditControlFileInteractor {

    @CanViewProjectAuditAndControl
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadAuditControlFileException::class)
    override fun download(projectId: Long, auditControlId: Long, fileId: Long): Pair<String, ByteArray> {
        val filePath = JemsFileType.AuditControl.generatePath(projectId, auditControlId)

        return filePersistence.existsFile(exactPath = filePath, fileId = fileId)
            .let { exists -> if (exists) filePersistence.downloadFile(type = JemsFileType.AuditControl, fileId = fileId) else null }
            ?: throw FileNotFound()
    }
}
