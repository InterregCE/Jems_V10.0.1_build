package io.cloudflight.jems.server.project.repository.file

import io.cloudflight.jems.server.project.entity.file.ProjectFileCategoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectFileCategoryRepository : JpaRepository<ProjectFileCategoryEntity, Long> {

    @Query(
        """
            SELECT IF(count(*) > 0, 'true', 'false')
            FROM project_file as projectFileEntity
            LEFT JOIN #{#entityName} as fileCategoryEntity ON projectFileEntity.id = fileCategoryEntity.file_id
            WHERE projectFileEntity.project_id = :projectId AND fileCategoryEntity.type = :categoryTypeString AND projectFileEntity.name = :fileName
             """,
        nativeQuery = true
    )
    fun fileNameExistsInCategory(projectId: Long, fileName: String, categoryTypeString: String) : Boolean
    fun existsByProjectFileProjectIdAndProjectFileName(projectId: Long, fileName: String) : Boolean
    fun deleteAllByCategoryIdFileId(fileId: Long)
    fun findAllByCategoryIdFileId(fileId: Long) : List<ProjectFileCategoryEntity>
}
