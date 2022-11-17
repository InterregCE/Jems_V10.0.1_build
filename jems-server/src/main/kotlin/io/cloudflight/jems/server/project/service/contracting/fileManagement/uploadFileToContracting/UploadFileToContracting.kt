package io.cloudflight.jems.server.project.service.contracting.fileManagement.uploadFileToContracting

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.minio.JemsProjectFileRepository
import io.cloudflight.jems.server.project.authorization.CanEditContractInfo
import io.cloudflight.jems.server.project.authorization.CanEditProjectMonitoring
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectContractingPartner
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType.*
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadFileToContracting(
    private val projectPersistence: ProjectPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val securityService: SecurityService,
    private val reportFilePersistence: ProjectReportFilePersistence,
    private val fileRepository: JemsProjectFileRepository,
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
    override fun uploadContractPartnerFile(projectId: Long, partnerId: Long, file: ProjectFile): JemsFileMetadata {
        if (projectId != partnerPersistence.getProjectIdForPartnerId(partnerId))
            throw PartnerNotFound(projectId = projectId, partnerId = partnerId)

        return uploadFileGeneric(projectId, partnerId, file, ContractPartnerDoc)
    }

    @CanEditProjectMonitoring
    @Transactional
    @ExceptionWrapper(UploadFileToContractingException::class)
    override fun uploadContractInternalFile(projectId: Long, file: ProjectFile) =
        uploadFileGeneric(projectId, null, file, ContractInternal)


    private fun uploadFileGeneric(projectId: Long, partnerId: Long?, file: ProjectFile, type: JemsFileType): JemsFileMetadata {
        val project = projectPersistence.getProjectSummary(projectId)
        validateProjectIsApproved(project)

        if (isFileTypeInvalid(file)) {
            throw FileTypeNotSupported()
        }

        with(type) {
            val location = generatePath(*listOf(projectId, partnerId).mapNotNull { it }.toLongArray())

            if (reportFilePersistence.existsFile(exactPath = location, fileName = file.name))
                throw FileAlreadyExists(file.name)

            val fileToSave = file.getFileMetadata(
                projectId = projectId,
                partnerId = partnerId,
                location = location,
                type = this,
                userId = securityService.getUserIdOrThrow(),
            )

            return fileRepository.persistProjectFile(fileToSave)
        }
    }

    private fun validateProjectIsApproved(project: ProjectSummary) {
        if (!project.status.isAlreadyApproved()) {
            throw ProjectNotApprovedException()
        }
    }

}
