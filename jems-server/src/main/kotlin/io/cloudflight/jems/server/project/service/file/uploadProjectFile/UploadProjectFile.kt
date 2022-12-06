package io.cloudflight.jems.server.project.service.file.uploadProjectFile

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.projectFileUploadSuccessOld
import io.cloudflight.jems.server.project.authorization.CanUploadFileInCategory
import io.cloudflight.jems.server.project.repository.file.ProjectFileTypeNotSupported
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.file.ProjectFilePersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategory
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType
import io.cloudflight.jems.server.project.service.file.model.ProjectFileMetadata
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.projectFileUploadFailed
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@OptIn(ExperimentalStdlibApi::class)
class UploadProjectFile(
    private val filePersistence: ProjectFilePersistence,
    private val projectPersistence: ProjectPersistence,
    private val userPersistence: UserPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val workPackagePersistence: WorkPackagePersistence,
    private val securityService: SecurityService,
    private val auditPublisher: ApplicationEventPublisher
) : UploadProjectFileInteractor {

    @CanUploadFileInCategory
    @Transactional
    @ExceptionWrapper(UploadFileExceptions::class)
    override fun upload(
        projectId: Long, projectFileCategory: ProjectFileCategory, projectFile: ProjectFile
    ): ProjectFileMetadata {
        throwIfFileTypeIsNotAllowed(projectFile)
        throwIfUploadToCategoryIsNotAllowed(projectFileCategory)
        projectPersistence.throwIfNotExists(projectId)
        userPersistence.throwIfNotExists(securityService.currentUser?.user?.id!!)
        throwIfCategoryIdNotExists(projectFileCategory, projectId)
        return runCatching {
            filePersistence.throwIfFileNameExistsInCategory(projectId, projectFile.name, projectFileCategory)
            filePersistence.saveFileMetadata(
                projectId, securityService.currentUser?.user?.id!!, projectFile, projectFileCategory
            ).also { fileMetadata ->
                val location = filePersistence.saveFile(
                    projectId, fileMetadata.id, securityService.currentUser?.user?.id!!, projectFile
                )
                val project = projectPersistence.getProjectSummary(projectId)
                auditPublisher.publishEvent(
                    projectFileUploadSuccessOld(
                    this, fileMetadata, location, projectFileCategory, project
                )
                )
            }
        }.onFailure {
            auditPublisher.publishEvent(
                projectFileUploadFailed(
                    this, projectId, projectFile.name, projectFileCategory, securityService.currentUser?.user?.id!!
                )
            )
        }.getOrThrow()

    }

    private fun throwIfCategoryIdNotExists(fileTypeCategory: ProjectFileCategory, projectId: Long) {
        if (fileTypeCategory.id != null) {
            when (fileTypeCategory.type) {
                ProjectFileCategoryType.PARTNER -> partnerPersistence.throwIfNotExistsInProject(projectId, fileTypeCategory.id)
                ProjectFileCategoryType.INVESTMENT -> workPackagePersistence.throwIfInvestmentNotExistsInProject(projectId, fileTypeCategory.id)
                else -> Unit
            }
        }
    }

    private fun throwIfUploadToCategoryIsNotAllowed(fileTypeCategory: ProjectFileCategory) {
        if (fileTypeCategory.type == ProjectFileCategoryType.ALL ||
            fileTypeCategory.type == ProjectFileCategoryType.PARTNER && fileTypeCategory.id == null ||
            fileTypeCategory.type == ProjectFileCategoryType.INVESTMENT && fileTypeCategory.id == null
        ) throw UploadInCategoryIsNotAllowedExceptions()
    }

    private fun throwIfFileTypeIsNotAllowed(file: ProjectFile) {
        if (isFileTypeInvalid(file))
            throw ProjectFileTypeNotSupported()
    }
}
