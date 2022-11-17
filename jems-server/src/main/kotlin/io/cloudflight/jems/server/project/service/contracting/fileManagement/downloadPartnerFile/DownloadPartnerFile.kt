package io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadPartnerFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectContractingPartner
import io.cloudflight.jems.server.project.service.contracting.fileManagement.FileNotFound
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadPartnerFile(
    private val contractingFilePersistence: ProjectContractingFilePersistence,
) : DownloadPartnerFileInteractor {

    @CanRetrieveProjectContractingPartner
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadPartnerFileException::class)
    override fun downloadPartnerFile(partnerId: Long, fileId: Long): Pair<String, ByteArray> {
        return contractingFilePersistence.downloadFileByPartnerId(partnerId = partnerId, fileId = fileId)
            ?: throw FileNotFound()
    }

}
