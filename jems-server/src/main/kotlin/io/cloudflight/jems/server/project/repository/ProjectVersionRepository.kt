package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.entity.ProjectVersionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface ProjectVersionRepository : JpaRepository<ProjectVersionEntity, Long> {

    @Query("SELECT row_end FROM #{#entityName}  where project_id= :projectId and version= :version", nativeQuery = true)
    fun findTimestampByVersion(projectId: Long, version: String): Timestamp?

    fun findFirstByIdProjectIdOrderByCreatedAtDesc(projectId: Long): ProjectVersionEntity?

    fun findAllVersionsByIdProjectIdOrderByCreatedAtDesc(projectId: Long): List<ProjectVersionEntity>
}
