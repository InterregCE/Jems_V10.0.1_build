package io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadPartnerFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectContractingPartners
import io.cloudflight.jems.server.project.service.contracting.fileManagement.FileNotFound
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import io.cloudflight.jems.server.project.service.contracting.fileManagement.validatePartnerFile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadPartnerFile(
    private val contractingFilePersistence: ProjectContractingFilePersistence,
    private val filePersistence: JemsFilePersistence
) : DownloadPartnerFileInteractor {

    @CanRetrieveProjectContractingPartners
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadPartnerFileException::class)
    override fun downloadPartnerFile(projectId: Long, fileId: Long): Pair<String, ByteArray> {
        validatePartnerFile(filePersistence.getFileType(fileId, projectId))
        return contractingFilePersistence.downloadFile(projectId = projectId, fileId = fileId)
            ?: throw FileNotFound()
    }
}
