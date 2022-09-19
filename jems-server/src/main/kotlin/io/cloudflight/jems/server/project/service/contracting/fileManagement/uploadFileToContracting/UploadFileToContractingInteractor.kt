package io.cloudflight.jems.server.project.service.contracting.fileManagement.uploadFileToContracting

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata

interface UploadFileToContractingInteractor {

    fun uploadContract(projectId: Long, file: ProjectFile): ProjectReportFileMetadata

    fun uploadContractDocument(projectId: Long, file: ProjectFile): ProjectReportFileMetadata

    fun uploadContractPartnerFile(projectId: Long, partnerId: Long, file: ProjectFile): ProjectReportFileMetadata

    fun uploadContractInternalFile(projectId: Long, file: ProjectFile): ProjectReportFileMetadata

}
