package io.cloudflight.ems.service

import io.cloudflight.ems.dto.FileMetadata
import java.io.InputStream

interface FileStorageService {

    fun saveFile(stream: InputStream, fileMetadata: FileMetadata)

    fun getFile(projectId: Long, fileName: String): ByteArray

}
