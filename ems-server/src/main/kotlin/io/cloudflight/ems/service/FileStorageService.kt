package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputProjectFile
import io.cloudflight.ems.dto.FileMetadata
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
    fun downloadFile(projectId: Long, fileId: Long): Pair<String, ByteArray>

    /**
     * Paged list of all project files.
     */
    fun getFilesForProject(projectId: Long, page: Pageable): Page<OutputProjectFile>

    fun setDescription(projectId: Long, fileId: Long, description: String?): OutputProjectFile

    fun deleteFile(projectId: Long, fileId: Long)

}
