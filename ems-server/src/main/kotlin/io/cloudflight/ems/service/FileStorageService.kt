package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputProjectFile
import io.cloudflight.ems.dto.FileMetadata
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.io.InputStream

interface FileStorageService {

    fun saveFile(stream: InputStream, fileMetadata: FileMetadata)

    fun getFile(projectId: Long, fileName: String): ByteArray

    fun getFilesForProject(projectId: Long, page: Pageable): Page<OutputProjectFile>

    fun setDescription(projectId: Long, fileId: Long, description: String?): OutputProjectFile

}
