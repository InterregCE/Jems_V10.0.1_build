package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.entity.OptimizationProjectVersionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OptimizationProjectVersionRepository : JpaRepository<OptimizationProjectVersionEntity, Long> {

    @Modifying
    @Query(
        """
            INSERT INTO #{#entityName} (project_id, last_approved_version) values ( :projectId, localtimestamp(6))
        """,
        nativeQuery = true
    )
    fun saveOptimizationProjectVersion(projectId: Long)


    @Modifying
    @Query(
        """
            UPDATE #{#entityName} SET last_approved_version = localtimestamp(6) WHERE project_id = :projectId
        """,
        nativeQuery = true
    )
    fun updateOptimizationProjectVersion(projectId: Long)


    @Query(
        """
            SELECT installed_on FROM flyway_schema_history WHERE installed_rank = 1
        """,
        nativeQuery = true
    )
    fun getInitialMigrationTimeStamp(): LocalDateTime

}
