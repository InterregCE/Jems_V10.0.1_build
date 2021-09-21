package io.cloudflight.jems.server.project.repository.file

import io.cloudflight.jems.server.project.entity.file.ProjectFileEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectFileRepository : PagingAndSortingRepository<ProjectFileEntity, Long> {


    @Query(
        """
            SELECT projectFileEntity.*
            FROM #{#entityName} projectFileEntity
            LEFT JOIN project_file_category as fileCategoryEntity ON projectFileEntity.id = fileCategoryEntity.file_id
            WHERE projectFileEntity.project_id = :projectId AND fileCategoryEntity.type = :categoryTypeString
             """,
        countQuery = """
            SELECT count(projectFileEntity.id)
            FROM #{#entityName} projectFileEntity
            LEFT JOIN project_file_category as fileCategoryEntity ON projectFileEntity.id = fileCategoryEntity.file_id
            WHERE projectFileEntity.project_id = :projectId AND fileCategoryEntity.type = :categoryTypeString
            """,
        nativeQuery = true
    )
    fun findAllProjectFilesInCategory(projectId: Long, categoryTypeString: String, page: Pageable): Page<ProjectFileEntity>

    fun findAllByProjectId(projectId: Long, page: Pageable): Page<ProjectFileEntity>
}
