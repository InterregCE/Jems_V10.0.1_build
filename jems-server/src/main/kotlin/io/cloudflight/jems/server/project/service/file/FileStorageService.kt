package io.cloudflight.jems.server.project.service.file

import io.cloudflight.jems.api.project.dto.file.OutputProjectFile
import io.cloudflight.jems.api.project.dto.file.ProjectFileType
import io.cloudflight.jems.server.project.entity.file.FileMetadata
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.io.InputStream

const val PROJECT_FILES_BUCKET = "project-files"

interface FileStorageService {

    /**
     * Creates a new file entry in DB and store file on storage.
     */
    fun saveFile(stream: InputStream, fileMetadata: FileMetadata)

    /**
     * Will return name of the file and ByteArray of file content.
     */
    fun downloadFile(projectId: Long, fileId: Long, type: ProjectFileType): Pair<String, ByteArray>

    fun getFileDetail(projectId: Long, fileId: Long, type: ProjectFileType): OutputProjectFile

    /**
     * Paged list of all project files.
     */
    fun getFilesForProject(projectId: Long, type: ProjectFileType, page: Pageable): Page<OutputProjectFile>

    fun setDescription(projectId: Long, fileId: Long, type: ProjectFileType, description: String?): OutputProjectFile

    fun deleteFile(projectId: Long, fileId: Long, type: ProjectFileType)

}
