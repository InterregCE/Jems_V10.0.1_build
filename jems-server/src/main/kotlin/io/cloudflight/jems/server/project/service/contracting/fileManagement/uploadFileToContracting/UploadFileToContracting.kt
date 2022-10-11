package io.cloudflight.jems.server.project.service.contracting.fileManagement.uploadFileToContracting

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditContractInfo
import io.cloudflight.jems.server.project.authorization.CanEditProjectManagement
import io.cloudflight.jems.server.project.authorization.CanEditProjectMonitoring
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectContractingPartner
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType.*
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadFileToContracting(
    private val projectPersistence: ProjectPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val contractingFilePersistence: ProjectContractingFilePersistence,
    private val securityService: SecurityService,
): UploadFileToContractingInteractor {

    @CanEditContractInfo
    @Transactional
    @ExceptionWrapper(UploadFileToContractingException::class)
    override fun uploadContract(projectId: Long, file: ProjectFile) =
        uploadFileGeneric(projectId, null, file, Contract)

    @CanEditContractInfo
    @Transactional
    @ExceptionWrapper(UploadFileToContractingException::class)
    override fun uploadContractDocument(projectId: Long, file: ProjectFile) =
        uploadFileGeneric(projectId, null, file, ContractDoc)

    @CanUpdateProjectContractingPartner
    @Transactional
    @ExceptionWrapper(UploadFileToContractingException::class)
    override fun uploadContractPartnerFile(projectId: Long, partnerId: Long, file: ProjectFile): ProjectReportFileMetadata {
        if (projectId != partnerPersistence.getProjectIdForPartnerId(partnerId))
            throw PartnerNotFound(projectId = projectId, partnerId = partnerId)

        return uploadFileGeneric(projectId, partnerId, file, ContractPartnerDoc)
    }

    @CanEditProjectMonitoring
    @Transactional
    @ExceptionWrapper(UploadFileToContractingException::class)
    override fun uploadContractInternalFile(projectId: Long, file: ProjectFile) =
        uploadFileGeneric(projectId, null, file, ContractInternal)


    private fun uploadFileGeneric(projectId: Long, partnerId: Long?, file: ProjectFile, type: ProjectPartnerReportFileType): ProjectReportFileMetadata {
        val project = projectPersistence.getProjectSummary(projectId)

        if (!project.status.isAlreadyApproved()) {
            throw ProjectNotApprovedException()
        }

        if (isFileTypeInvalid(file)) {
            throw FileTypeNotSupported()
        }

        with(type) {
            return contractingFilePersistence.uploadFile(
                file = file.getFileMetadata(
                    projectId = projectId,
                    partnerId = partnerId,
                    location = generatePath(*listOf(projectId, partnerId).mapNotNull { it }.toLongArray()),
                    type = this,
                    userId = securityService.getUserIdOrThrow(),
                ),
            )
        }
    }

}
