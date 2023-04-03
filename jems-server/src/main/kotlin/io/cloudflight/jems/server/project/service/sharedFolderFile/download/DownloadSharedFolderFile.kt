package io.cloudflight.jems.server.project.service.sharedFolderFile.download

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.CanRetrieveSharedFolder
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadSharedFolderFile(
    private val filePersistence: JemsFilePersistence,
) : DownloadSharedFolderFileInteractor {

    @CanRetrieveSharedFolder
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadSharedFolderFileException::class)
    override fun download(projectId: Long, fileId: Long): Pair<String, ByteArray> {
        if (!filePersistence.existsFileByProjectIdAndFileIdAndFileTypeIn(projectId, fileId, setOf(JemsFileType.SharedFolder))) {
            throw FileNotFound()
        }

        return filePersistence.downloadFile(JemsFileType.SharedFolder, fileId = fileId) ?: throw FileNotFound()
    }
}
