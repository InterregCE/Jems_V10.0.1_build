package io.cloudflight.jems.server.project.repository.file

import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.project.entity.file.ProjectFileCategoryEntity
import io.cloudflight.jems.server.project.entity.file.ProjectFileCategoryId
import io.cloudflight.jems.server.project.entity.file.ProjectFileEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.service.file.ProjectFilePersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategory
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType
import io.cloudflight.jems.server.project.service.file.model.ProjectFileMetadata
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

const val PROJECT_FILES_BUCKET = "project-files"

@Repository
class ProjectFilePersistenceProvider(
    private val storage: MinioStorage,
    private val userRepository: UserRepository,
    private val projectRepository: ProjectRepository,
    private val projectFileRepository: ProjectFileRepository,
    private val projectFileCategoryRepository: ProjectFileCategoryRepository
) : ProjectFilePersistence {

    override fun saveFile(projectId: Long, fileId: Long, userId: Long, projectFile: ProjectFile) =
        storage.saveFile(
            PROJECT_FILES_BUCKET,
            getObjectPath(projectId, fileId, projectFile.name),
            projectFile.size, projectFile.stream
        )

    @Transactional
    override fun saveFileMetadata(
        projectId: Long, userId: Long, projectFile: ProjectFile, projectFileCategory: ProjectFileCategory
    ): ProjectFileMetadata =
        projectFileRepository.save(
            ProjectFileEntity(
                name = projectFile.name,
                project = projectRepository.getOne(projectId),
                user = userRepository.getOne(userId),
                description = null, size = projectFile.size, updated = ZonedDateTime.now()
            )
        ).also { savedEntity ->
            projectFileCategoryRepository.saveAll(
                projectFileCategory.getAllCategoryTypeStrings().map { categoryLabel ->
                    ProjectFileCategoryEntity(ProjectFileCategoryId(savedEntity.id, categoryLabel), savedEntity)
                }
            )
        }.toModel()

    @Transactional(readOnly = true)
    override fun getFileMetadata(fileId: Long): ProjectFileMetadata =
        projectFileRepository.findById(fileId).orElseThrow { ProjectFileNotFoundException() }.toModel()

    @Transactional(readOnly = true)
    override fun getFileCategoryTypeSet(fileId: Long): Set<ProjectFileCategoryType> =
        projectFileCategoryRepository.findAllByCategoryIdFileId(fileId).toFileCategoryTypeSet()

    @Transactional(readOnly = true)
    override fun listFileMetadata(
        projectId: Long, fileCategory: ProjectFileCategory, page: Pageable
    ): Page<ProjectFileMetadata> =
        if (fileCategory.type == ProjectFileCategoryType.ALL)
            projectFileRepository.findAllByProjectId(projectId, page).toModel()
        else
            projectFileRepository.findAllProjectFilesInCategory(projectId, fileCategory.getCategoryTypeString(), page)
                .toModel()


    @Transactional(readOnly = true)
    override fun throwIfFileNameExistsInCategory(
        projectId: Long, fileName: String, fileCategory: ProjectFileCategory
    ) {
        if (fileCategory.type == ProjectFileCategoryType.ALL &&
            projectFileCategoryRepository.existsByProjectFileProjectIdAndProjectFileName(projectId, fileName)
            ||
            fileCategory.type != ProjectFileCategoryType.ALL &&
            projectFileCategoryRepository.fileNameExistsInCategory(
                projectId, fileName, fileCategory.getCategoryTypeString()
            )
        ) throw FileNameAlreadyExistsException()
    }

    override fun getFile(projectId: Long, fileId: Long, fileName: String): ByteArray =
        storage.getFile(PROJECT_FILES_BUCKET, getObjectPath(projectId, fileId, fileName))

    @Transactional
    override fun deleteFile(projectId: Long, fileId: Long, fileName: String) {
        projectFileCategoryRepository.deleteAllByCategoryIdFileId(fileId)
        projectFileRepository.deleteById(fileId)
        storage.deleteFile(PROJECT_FILES_BUCKET, getObjectPath(projectId, fileId, fileName))
    }

    @Transactional
    override fun setFileDescription(fileId: Long, description: String?): ProjectFileMetadata =
        projectFileRepository.findById(fileId).orElseThrow { ProjectFileNotFoundException() }
            .also { it.description = description }.toModel()


    private fun ProjectFileCategory.getCategoryTypeString(): String =
        when (this.type) {
            ProjectFileCategoryType.PARTNER -> if (id != null) partnerIdLabel(id) else this.type.name
            ProjectFileCategoryType.INVESTMENT -> if (id != null) investmentIdLabel(id) else this.type.name
            else -> this.type.name
        }

    private fun ProjectFileCategory.getAllCategoryTypeStrings(): Set<String> =
        this.type.getParentCategoryTypeStringSet().also {
            if (id != null) {
                when (this.type) {
                    ProjectFileCategoryType.PARTNER -> it.add(partnerIdLabel(id))
                    ProjectFileCategoryType.INVESTMENT -> it.add(investmentIdLabel(id))
                    else -> Unit
                }
            }
        }

    private fun ProjectFileCategoryType.getParentCategoryTypeStringSet(): MutableSet<String> =
        if (this.parent == null || this.parent == ProjectFileCategoryType.ALL)
            mutableSetOf(this.name)
        else this.parent.getParentCategoryTypeStringSet().also { it.add(this.name) }

    private fun getObjectPath(projectId: Long, fileId: Long, fileName: String): String =
        "project-${projectId}/$fileId/${fileName}"

    private fun partnerIdLabel(id: Long) =
        "${ProjectFileCategoryType.PARTNER.name}=$id"

    private fun investmentIdLabel(id: Long) =
        "${ProjectFileCategoryType.INVESTMENT.name}=$id"
}
