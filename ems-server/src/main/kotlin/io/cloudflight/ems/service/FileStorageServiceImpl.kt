package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputProjectFile
import io.cloudflight.ems.dto.FileMetadata
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.entity.ProjectFile
import io.cloudflight.ems.exception.DuplicateFileException
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.repository.UserRepository
import io.cloudflight.ems.repository.MinioStorage
import io.cloudflight.ems.repository.ProjectFileRepository
import io.cloudflight.ems.repository.ProjectRepository
import io.cloudflight.ems.security.service.SecurityService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import java.time.ZonedDateTime
import java.util.Optional

@Service
class FileStorageServiceImpl(
    private val auditService: AuditService,
    private val storage: MinioStorage,
    private val projectFileRepository: ProjectFileRepository,
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
    private val securityService: SecurityService
) : FileStorageService {

    @Transactional
    override fun saveFile(stream: InputStream, fileMetadata: FileMetadata) {
        val potentialDuplicate = getFileByName(fileMetadata.projectId, fileMetadata.name)
        if (potentialDuplicate.isPresent) {
            with(potentialDuplicate.get()) {
                auditService.logEvent(
                    Audit.projectFileUploadFailed(
                        securityService.currentUser, fileMetadata.projectId, fileMetadata.name
                    )
                )
                throw DuplicateFileException(project.id, name, updated)
            }
        }

        val project = projectRepository.findById(fileMetadata.projectId)
            .orElseThrow { throw ResourceNotFoundException() }

        val author = userRepository.findById(securityService.currentUser?.user?.id!!)
            .orElseThrow { throw ResourceNotFoundException() }

        val filePath = getFilePath(fileMetadata.projectId, fileMetadata.name)
        val projectFileEntity = ProjectFile(
            id = null,
            bucket = PROJECT_FILES_BUCKET,
            identifier = filePath,
            name = fileMetadata.name,
            project = project,
            author = author,
            description = null,
            size = fileMetadata.size,
            updated = ZonedDateTime.now()
        )

        projectFileRepository.save(projectFileEntity)
        storage.saveFile(PROJECT_FILES_BUCKET, filePath, fileMetadata.size, stream)

        auditService.logEvent(
            Audit.projectFileUploadedSuccessfully(
                securityService.currentUser,
                fileMetadata.projectId,
                projectFileEntity
            )
        )
    }

    override fun downloadFile(projectId: Long, fileId: Long): Pair<String, ByteArray> {
        val projectFile = getFile(projectId, fileId)
        return Pair(
            projectFile.name,
            storage.getFile(projectFile.bucket, projectFile.identifier)
        )
    }

    @Transactional(readOnly = true)
    override fun getFileDetail(projectId: Long, fileId: Long): OutputProjectFile {
        return getFile(projectId, fileId).toOutputProjectFile()
    }

    @Transactional(readOnly = true)
    override fun getFilesForProject(projectId: Long, page: Pageable): Page<OutputProjectFile> {
        return projectFileRepository.findAllByProjectId(projectId, page).map { it.toOutputProjectFile() }
    }

    @Transactional
    override fun setDescription(projectId: Long, fileId: Long, description: String?): OutputProjectFile {
        val projectFile = getFile(projectId, fileId)
        val oldDescription = projectFile.description

        projectFile.description = description
        val savedProjectFile = projectFileRepository.save(projectFile).toOutputProjectFile()

        auditService.logEvent(
            Audit.projectFileDescriptionChanged(
                securityService.currentUser,
                projectId,
                projectFile,
                oldDescription
            )
        )

        return savedProjectFile
    }

    @Transactional
    override fun deleteFile(projectId: Long, fileId: Long) {
        val file = getFile(projectId, fileId)

        storage.deleteFile(PROJECT_FILES_BUCKET, file.identifier)
        projectFileRepository.delete(file)

        auditService.logEvent(Audit.projectFileDeleted(securityService.currentUser, projectId, file))
    }

    private fun getFile(projectId: Long, fileId: Long): ProjectFile {
        val result = projectFileRepository.findFirstByProjectIdAndId(projectId = projectId, id = fileId)
        if (result.isEmpty) {
            throw ResourceNotFoundException()
        }
        return result.get()
    }

    private fun getFileByName(projectId: Long, name: String): Optional<ProjectFile> {
        return projectFileRepository.findFirstByProjectIdAndName(projectId = projectId, name = name)
    }

    private fun getFilePath(projectIdentifier: Long, fileIdentifier: String): String {
        return "project-$projectIdentifier/$fileIdentifier"
    }

}
