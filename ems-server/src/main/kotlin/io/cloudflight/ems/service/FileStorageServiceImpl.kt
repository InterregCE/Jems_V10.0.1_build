package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputProjectFile
import io.cloudflight.ems.dto.FileMetadata
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.entity.Project
import io.cloudflight.ems.entity.ProjectFile
import io.cloudflight.ems.exception.DataValidationException
import io.cloudflight.ems.exception.DuplicateFileException
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.repository.MinioStorage
import io.cloudflight.ems.repository.ProjectFileRepository
import io.cloudflight.ems.service.ProjectFileDtoUtilClass.Companion.getDtoFrom
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import java.time.ZonedDateTime
import java.util.Optional

const val PROJECT_FILES_BUCKET = "project-files"

@Service
class FileStorageServiceImpl(
    private val auditService: AuditService,
    private val storage: MinioStorage,
    private val repository: ProjectFileRepository
): FileStorageService {

    @Transactional
    override fun saveFile(stream: InputStream, fileMetadata: FileMetadata) {
        val potentialDuplicate = getFileByName(fileMetadata.projectId, fileMetadata.name)
        if (potentialDuplicate.isPresent) {
            with (potentialDuplicate.get()) {
                throw DuplicateFileException(project?.id, name, updated)
            }
        }

        val filePath = getFilePath(fileMetadata.projectId, fileMetadata.name)
        val projectFileEntity = ProjectFile(
            id = null,
            bucket = PROJECT_FILES_BUCKET,
            identifier = filePath,
            name = fileMetadata.name,
            project = Project(id = fileMetadata.projectId, acronym = null, submissionDate = null),
            description = null,
            size = fileMetadata.size,
            updated = ZonedDateTime.now())

        repository.save(projectFileEntity)
        storage.saveFile(PROJECT_FILES_BUCKET, filePath, fileMetadata.size, stream)
    }

    override fun downloadFile(projectId: Long, fileId: Long): Pair<String, ByteArray> {
        val projectFile = getFile(projectId, fileId)
        return Pair(
            projectFile.name,
            storage.getFile(PROJECT_FILES_BUCKET, projectFile.identifier))
    }

    @Transactional(readOnly = true)
    override fun getFilesForProject(projectId: Long, page: Pageable): Page<OutputProjectFile> {
        return repository.findAllByProject_Id(projectId, page).map { getDtoFrom(it) }
    }

    @Transactional
    override fun setDescription(projectId: Long, fileId: Long, description: String?): OutputProjectFile {
        val projectFile = getFile(projectId, fileId)

        if (description != null && description.length > 100) {
            throw DataValidationException(mapOf("description" to listOf(DataValidationException.STRING_LONG)))
        }

        projectFile.description = description
        return getDtoFrom(repository.save(projectFile))
    }

    @Transactional
    override fun deleteFile(projectId: Long, fileId: Long) {
        val file = getFile(projectId, fileId)
        auditService.logEvent(Audit.projectFileDeleted(projectId, file))
        storage.deleteFile(PROJECT_FILES_BUCKET, file.identifier)
        repository.delete(file)
    }

    private fun getFile(projectId: Long, fileId: Long): ProjectFile {
        val result = repository.findFirstByProject_IdAndId(projectId = projectId, id = fileId)
        if (result.isEmpty) {
            throw ResourceNotFoundException()
        }
        return result.get()
    }

    private fun getFileByName(projectId: Long, name: String): Optional<ProjectFile> {
        return repository.findFirstByProject_IdAndName(projectId = projectId, name = name)
    }

    private fun getFilePath(projectIdentifier: Long, fileIdentifier: String): String {
        return "project-$projectIdentifier/$fileIdentifier"
    }

}
