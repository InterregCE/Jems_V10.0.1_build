package io.cloudflight.jems.server.project.service.file

import io.cloudflight.jems.api.project.dto.file.OutputProjectFile
import io.cloudflight.jems.api.project.dto.file.ProjectFileType
import io.cloudflight.jems.server.project.entity.file.FileMetadata
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.project.entity.file.ProjectFile
import io.cloudflight.jems.server.common.exception.DuplicateFileException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.user.repository.UserRepository
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.project.repository.ProjectFileRepository
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.authentication.service.SecurityService
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
        val potentialDuplicate = getFileByName(fileMetadata)
        if (potentialDuplicate.isPresent) {
            with(potentialDuplicate.get()) {
                auditService.logEvent(
                    projectFileUploadFailed(fileMetadata.projectId, fileMetadata.name)
                )
                throw DuplicateFileException(project.id, name, updated)
            }
        }

        val project = projectRepository.findById(fileMetadata.projectId)
            .orElseThrow { ResourceNotFoundException() }

        val author = userRepository.findById(securityService.currentUser?.user?.id!!)
            .orElseThrow { ResourceNotFoundException() }

        val filePath = getFilePath(fileMetadata)
        val projectFileEntity = ProjectFile(
            bucket = PROJECT_FILES_BUCKET,
            identifier = filePath,
            name = fileMetadata.name,
            project = project,
            author = author,
            type = fileMetadata.type,
            description = null,
            size = fileMetadata.size,
            updated = ZonedDateTime.now()
        )

        projectFileRepository.save(projectFileEntity)
        storage.saveFile(PROJECT_FILES_BUCKET, filePath, fileMetadata.size, stream)

        auditService.logEvent(
            projectFileUploadedSuccessfully(
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
    override fun getFilesForProject(projectId: Long, type: ProjectFileType, page: Pageable): Page<OutputProjectFile> {
        return projectFileRepository.findAllByProjectIdAndType(projectId, type, page).map { it.toOutputProjectFile() }
    }

    @Transactional
    override fun setDescription(projectId: Long, fileId: Long, description: String?): OutputProjectFile {
        val projectFile = getFile(projectId, fileId)
        val oldDescription = projectFile.description

        projectFile.description = description
        val savedProjectFile = projectFileRepository.save(projectFile).toOutputProjectFile()

        auditService.logEvent(
            projectFileDescriptionChangedAudit(
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

        auditService.logEvent(projectFileDeleted(projectId, file))
    }

    private fun getFile(projectId: Long, fileId: Long): ProjectFile {
        val result = projectFileRepository.findFirstByProjectIdAndId(projectId = projectId, id = fileId)
        if (result.isEmpty) {
            throw ResourceNotFoundException("project_file")
        }
        return result.get()
    }

    private fun getFileByName(fileMetadata: FileMetadata): Optional<ProjectFile> {
        with(fileMetadata) {
            return projectFileRepository
                .findFirstByProjectIdAndNameAndType(projectId, name, type)
        }
    }

    private fun getFilePath(fileMetadata: FileMetadata): String {
        return "project-${fileMetadata.projectId}/${fileMetadata.type}/${fileMetadata.name}"
    }

    private fun projectFileDescriptionChangedAudit(projectId: Long, file: ProjectFile, oldDescription: String?): AuditCandidate =
        AuditCandidate(
            action = AuditAction.PROJECT_FILE_DESCRIPTION_CHANGED,
            projectId = projectId.toString(),
            description = "description of document ${file.name} in project application $projectId has changed from $oldDescription to ${file.description}"
        )

    private fun projectFileDeleted(projectId: Long, file: ProjectFile): AuditCandidate =
        AuditCandidate(
            action = AuditAction.PROJECT_FILE_DELETED,
            projectId = projectId.toString(),
            description = "document ${file.name} deleted from application $projectId"
        )

    private fun projectFileUploadedSuccessfully(projectId: Long, file: ProjectFile): AuditCandidate =
        AuditCandidate(
            action = AuditAction.PROJECT_FILE_UPLOADED_SUCCESSFULLY,
            projectId = projectId.toString(),
            description = "document ${file.name} uploaded to project application $projectId"
        )

    private fun projectFileUploadFailed(projectId: Long, fileName: String?): AuditCandidate =
        AuditCandidate(
            action = AuditAction.PROJECT_FILE_UPLOAD_FAILED,
            projectId = projectId.toString(),
            description = "FAILED upload of document $fileName to project application $projectId"
        )
}
