package io.cloudflight.jems.server.project.service.file.list_project_file_metadata

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.ProjectFileAuthorization
import io.cloudflight.jems.server.project.service.file.ProjectFilePersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategory
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType
import io.cloudflight.jems.server.project.service.file.model.ProjectFileMetadata
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListProjectFileMetadata(
    private val filePersistence: ProjectFilePersistence,
    private val authorization: ProjectFileAuthorization
) : ListProjectFileMetadataInteractor {

    @Transactional
    @ExceptionWrapper(ListProjectFileMetadataExceptions::class)
    override fun list(
        projectId: Long, projectFileCategory: ProjectFileCategory, page: Pageable
    ): Page<ProjectFileMetadata> {
        val files = with(authorization.getRetrievableCategories(projectId)) {
            if (hasNoAccess(this, projectFileCategory)) return Page.empty()
            filePersistence.listFileMetadata(projectId, getFileCategory(this, projectFileCategory), page)
        }
        return files.fillInCategory(
            categoriesMap = filePersistence.getCategoriesMap(files.mapTo(HashSet()) { it.id })
        )
    }


    private fun hasNoAccess(
        retrievableCategories: Set<ProjectFileCategoryType>, projectFileCategory: ProjectFileCategory
    ): Boolean =
        if (projectFileCategory.type == ProjectFileCategoryType.ALL) retrievableCategories.isEmpty()
        else !retrievableCategories.contains(projectFileCategory.type)

    private fun getFileCategory(
        retrievableCategories: Set<ProjectFileCategoryType>, projectFileCategory: ProjectFileCategory
    ): ProjectFileCategory =
        when {
            projectFileCategory.type != ProjectFileCategoryType.ALL -> projectFileCategory
            else -> retrievableCategories.filterTo(hashSetOf()) { it == ProjectFileCategoryType.ASSESSMENT || it == ProjectFileCategoryType.APPLICATION }
                .let { categoryTypes ->
                    if (categoryTypes.size == 2) ProjectFileCategory(ProjectFileCategoryType.ALL, null)
                    else categoryTypes.map { ProjectFileCategory(it, null) }.first()
                }
        }

    private fun Page<ProjectFileMetadata>.fillInCategory(categoriesMap: Map<Long, Set<ProjectFileCategoryType>>) =
        this.onEach { it.category = categoriesMap[it.id]?.getCategory() }

    private fun Set<ProjectFileCategoryType>.getCategory(): ProjectFileCategoryType? =
        if (this.contains(ProjectFileCategoryType.PARTNER))
            ProjectFileCategoryType.PARTNER
        else if (this.contains(ProjectFileCategoryType.INVESTMENT))
            ProjectFileCategoryType.INVESTMENT
        else if (this.contains(ProjectFileCategoryType.APPLICATION))
            ProjectFileCategoryType.APPLICATION
        else if (this.contains(ProjectFileCategoryType.MODIFICATION))
            ProjectFileCategoryType.MODIFICATION
        else if (this.contains(ProjectFileCategoryType.ASSESSMENT))
            ProjectFileCategoryType.ASSESSMENT
        else
            null

}
