package io.cloudflight.jems.server.project.service.contracting.fileManagement.uploadFileToContracting

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.authorization.CanEditContractsAndAgreements
import io.cloudflight.jems.server.project.authorization.CanEditProjectMonitoring
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectContractingPartner
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingSection
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadFileToContracting(
    private val projectPersistence: ProjectPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val securityService: SecurityService,
    private val filePersistence: JemsFilePersistence,
    private val fileRepository: JemsProjectFileService,
    private val validator: ContractingValidator
) : UploadFileToContractingInteractor {

    @CanEditContractsAndAgreements
    @Transactional
    @ExceptionWrapper(UploadFileToContractingException::class)
    override fun uploadContract(projectId: Long, file: ProjectFile): JemsFileMetadata {
        validator.validateSectionLock(ProjectContractingSection.ContractsAgreements, projectId)
        return uploadFileGeneric(projectId, null, file, JemsFileType.Contract)
    }

    @CanEditContractsAndAgreements
    @Transactional
    @ExceptionWrapper(UploadFileToContractingException::class)
    override fun uploadContractDocument(projectId: Long, file: ProjectFile): JemsFileMetadata {
        validator.validateSectionLock(ProjectContractingSection.ContractsAgreements, projectId)
        return uploadFileGeneric(projectId, null, file, JemsFileType.ContractDoc)
    }


    @CanUpdateProjectContractingPartner
    @Transactional
    @ExceptionWrapper(UploadFileToContractingException::class)
    override fun uploadContractPartnerFile(projectId: Long, partnerId: Long, file: ProjectFile): JemsFileMetadata {
        validator.validatePartnerLock(partnerId)
        if (projectId != partnerPersistence.getProjectIdForPartnerId(partnerId))
            throw PartnerNotFound(projectId = projectId, partnerId = partnerId)

        return uploadFileGeneric(projectId, partnerId, file, JemsFileType.ContractPartnerDoc)
    }

    @CanEditProjectMonitoring
    @Transactional
    @ExceptionWrapper(UploadFileToContractingException::class)
    override fun uploadContractInternalFile(projectId: Long, file: ProjectFile) =
        uploadFileGeneric(projectId, null, file, JemsFileType.ContractInternal)


    private fun uploadFileGeneric(
        projectId: Long,
        partnerId: Long?,
        file: ProjectFile,
        type: JemsFileType
    ): JemsFileMetadata {
        val project = projectPersistence.getProjectSummary(projectId)
        validateProjectIsApproved(project)

        if (isFileTypeInvalid(file)) {
            throw FileTypeNotSupported()
        }

        with(type) {
            val location = generatePath(*listOf(projectId, partnerId).mapNotNull { it }.toLongArray())

            if (filePersistence.existsFile(exactPath = location, fileName = file.name))
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
