package io.cloudflight.jems.server.project.service.contracting.fileManagement.uploadFileToContracting

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata

interface UploadFileToContractingInteractor {

    fun uploadContract(projectId: Long, file: ProjectFile): JemsFileMetadata

    fun uploadContractDocument(projectId: Long, file: ProjectFile): JemsFileMetadata

    fun uploadContractPartnerFile(projectId: Long, partnerId: Long, file: ProjectFile): JemsFileMetadata

    fun uploadContractInternalFile(projectId: Long, file: ProjectFile): JemsFileMetadata

}
