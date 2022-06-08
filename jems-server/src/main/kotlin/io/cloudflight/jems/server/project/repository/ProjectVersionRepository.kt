package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.entity.ProjectVersionEntity
import io.cloudflight.jems.server.project.entity.ProjectVersionId
import io.cloudflight.jems.server.project.entity.ProjectVersionRow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface ProjectVersionRepository : JpaRepository<ProjectVersionEntity, ProjectVersionId> {

    @Query("SELECT IF(row_end IS NULL, localtimestamp(6), row_end) FROM #{#entityName}  where project_id= :projectId and version= :version", nativeQuery = true)
    fun findTimestampByVersion(projectId: Long, version: String): Timestamp?

    @Query("SELECT version FROM #{#entityName} WHERE project_id= :projectId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    fun findLatestVersion(projectId: Long): String?

    @Query("""
        SELECT projectVersion.row_end
        FROM #{#entityName} as projectVersion
        WHERE projectVersion.project_id = :projectId
        AND (SELECT ps.status
             FROM project FOR SYSTEM_TIME AS OF ( IF(projectVersion.row_end IS NULL, localtimestamp(6), projectVersion.row_end)) AS p
             LEFT JOIN project_status as ps on p.project_status_id = ps.id
             WHERE p.id= projectVersion.project_id LIMIT 1) = :status
        ORDER BY projectVersion.created_at DESC LIMIT 1
    """, nativeQuery = true)
    fun findLastTimestampByStatus(projectId: Long, status: String): Timestamp?

    @Modifying
    @Query("UPDATE #{#entityName} SET row_end = localtimestamp(6) WHERE project_id= :projectId AND row_end IS NULL", nativeQuery = true)
    fun endCurrentVersion(projectId: Long)

    @Modifying
    @Query("UPDATE #{#entityName} SET row_end = null WHERE project_id= :projectId AND row_end = :timestamp ", nativeQuery = true)
    fun setVersionAsCurrent(projectId: Long, timestamp: Timestamp)

    @Query(
        """
        SELECT
        projectVersion.version,
        projectVersion.project_id as projectId,
        projectVersion.row_end as rowEnd,
        projectVersion.created_at as createdAt,
        account.id as userId,
        account.email,
        account.name,
        account.surname,
        account.user_status as userStatus,
        account_role.id as roleId,
        account_role.name as roleName,

        (
            SELECT ps.status
            FROM project FOR SYSTEM_TIME AS OF ( IF(projectVersion.row_end IS NULL, localtimestamp(6), projectVersion.row_end)) AS p
            LEFT JOIN project_status as ps on p.project_status_id = ps.id
            WHERE p.id= projectVersion.project_id
       ) as status

        FROM #{#entityName} as projectVersion
        LEFT JOIN account on account.id = projectVersion.account_id
        LEFT JOIN account_role on account_role.id = account.account_role_id
        WHERE projectVersion.project_id = :projectId
        ORDER BY projectVersion.created_at DESC
    """, nativeQuery = true)
    fun findAllVersionsByProjectId(projectId: Long): List<ProjectVersionRow>

    @Query(
        """
        SELECT
        projectVersion.version,
        projectVersion.project_id as projectId,
        projectVersion.row_end as rowEnd,
        projectVersion.created_at as createdAt,
        account.id as userId,
        account.email,
        account.name,
        account.surname,
        account.user_status as userStatus,
        account_role.id as roleId,
        account_role.name as roleName,

        (
            SELECT ps.status
            FROM project FOR SYSTEM_TIME AS OF ( IF(projectVersion.row_end IS NULL, localtimestamp(6), projectVersion.row_end)) AS p
            LEFT JOIN project_status as ps on p.project_status_id = ps.id
            WHERE p.id= projectVersion.project_id
       ) as status

        FROM #{#entityName} as projectVersion
        LEFT JOIN account on account.id = projectVersion.account_id
        LEFT JOIN account_role on account_role.id = account.account_role_id
        ORDER BY projectVersion.created_at DESC
    """, nativeQuery = true
    )
    fun findAllVersions(): List<ProjectVersionRow>
}
