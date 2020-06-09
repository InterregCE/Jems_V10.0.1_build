package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputProjectFile
import io.cloudflight.ems.dto.FileMetadata
import io.cloudflight.ems.entity.Project
import io.cloudflight.ems.entity.ProjectFile
import io.cloudflight.ems.repository.MinioStorage
import io.cloudflight.ems.repository.ProjectFileRepository
import io.cloudflight.ems.service.ProjectFileDtoUtilClass.Companion.getDtoFrom
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import java.time.ZonedDateTime

const val PROJECT_FILES_BUCKET = "project-files"

@Service
class FileStorageServiceImpl(
    private val storage: MinioStorage,
    private val repository: ProjectFileRepository
): FileStorageService {

    @Transactional
    override fun saveFile(stream: InputStream, fileMetadata: FileMetadata) {
        val filePath = getFilePath(fileMetadata.projectId, fileMetadata.name)
        val projectFileEntity = ProjectFile(
            id = null,
            bucket = PROJECT_FILES_BUCKET,
            identifier = filePath,
            project = Project(id = fileMetadata.projectId, acronym = null, submissionDate = null),
            description = null,
            size = fileMetadata.size,
            updated = ZonedDateTime.now())

        repository.save(projectFileEntity)
        storage.saveFile(PROJECT_FILES_BUCKET, filePath, fileMetadata.size, stream)
    }

    /*
    @Transactional(readOnly = true)
    override fun listFilesByProject(projectId: String): Iterable<Result<Item>> {
        return storage.listFilesPerProject(projectId)
    }
    */

    override fun getFile(projectId: Long, fileName: String): ByteArray {
        return storage.getFile(PROJECT_FILES_BUCKET, getFilePath(projectId, fileName))
    }

    @Transactional(readOnly = true)
    override fun getFilesForProject(projectId: Long, page: Pageable): Page<OutputProjectFile> {
        return repository.findAllByProject_Id(projectId, page).map { getDtoFrom(it) }
    }

    private fun getFilePath(projectIdentifier: Long, fileIdentifier: String): String {
        return "project-$projectIdentifier/$fileIdentifier"
    }

}
