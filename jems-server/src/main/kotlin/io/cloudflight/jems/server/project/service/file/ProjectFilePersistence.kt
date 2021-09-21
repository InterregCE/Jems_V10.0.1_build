package io.cloudflight.jems.server.project.service.file

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategory
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType
import io.cloudflight.jems.server.project.service.file.model.ProjectFileMetadata
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectFilePersistence {

    fun saveFile(projectId: Long, fileId: Long, userId: Long, projectFile: ProjectFile)

    fun saveFileMetadata(
        projectId: Long, userId: Long, projectFile: ProjectFile, projectFileCategory: ProjectFileCategory
    ): ProjectFileMetadata

    fun getFileMetadata(fileId: Long): ProjectFileMetadata

    fun getFileCategoryTypeSet(fileId: Long): Set<ProjectFileCategoryType>

    fun listFileMetadata(projectId: Long, fileCategory: ProjectFileCategory, page: Pageable): Page<ProjectFileMetadata>

    fun throwIfFileNameExistsInCategory(projectId: Long, fileName: String, fileCategory: ProjectFileCategory)

    fun getFile(projectId: Long, fileId: Long, fileName: String): ByteArray

    fun deleteFile(projectId: Long, fileId: Long, fileName: String)

    fun setFileDescription(fileId: Long, description: String?): ProjectFileMetadata

}
