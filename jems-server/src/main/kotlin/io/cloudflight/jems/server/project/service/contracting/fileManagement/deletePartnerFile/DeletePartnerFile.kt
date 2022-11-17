package io.cloudflight.jems.server.project.service.contracting.fileManagement.deletePartnerFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectContractingPartner
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePartnerFile(
    private val contractingFilePersistence: ProjectContractingFilePersistence,
): DeletePartnerFileInteractor {

    @CanUpdateProjectContractingPartner
    @Transactional
    @ExceptionWrapper(DeleteContractingPartnerFileException::class)
    override fun delete(partnerId: Long, fileId: Long) {
        contractingFilePersistence.deleteFileByPartnerId(partnerId = partnerId, fileId = fileId)
    }
}
