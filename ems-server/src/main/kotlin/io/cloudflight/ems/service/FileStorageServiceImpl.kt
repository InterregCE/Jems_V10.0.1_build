package io.cloudflight.ems.service

import io.cloudflight.ems.dto.FileMetadata
import io.cloudflight.ems.repository.MinioStorage
import org.springframework.stereotype.Service
import java.io.InputStream

const val PROJECT_FILES_BUCKET = "project-files"

@Service
class FileStorageServiceImpl(
    private val storage: MinioStorage
): FileStorageService {

    // TODO done with next MP2-57 add save to mariadb
    //@Transactional
    override fun saveFile(stream: InputStream, fileMetadata: FileMetadata) {
        val filePath = getFilePath(fileMetadata.projectId, fileMetadata.name)
        // TODO save also to our DB here
        storage.saveFile(PROJECT_FILES_BUCKET, filePath, fileMetadata.size, stream)
    }

    /*
    @Transactional(readOnly = true)
    override fun listFilesByProject(projectId: String): Iterable<Result<Item>> {
        return storage.listFilesPerProject(projectId)
    }
    */

    // TODO done with next MP2-57 change to read from mariadb
    //@Transactional(readOnly = true)
    override fun getFile(projectId: Long, fileName: String): ByteArray {
        return storage.getFile(PROJECT_FILES_BUCKET, getFilePath(projectId, fileName))
    }

    private fun getFilePath(projectIdentifier: Long, fileIdentifier: String): String {
        return "project-$projectIdentifier/$fileIdentifier"
    }

}
