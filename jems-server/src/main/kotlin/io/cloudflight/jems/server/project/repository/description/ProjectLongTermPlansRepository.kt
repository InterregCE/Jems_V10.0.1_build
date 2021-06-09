package io.cloudflight.jems.server.project.repository.description

import io.cloudflight.jems.server.project.entity.description.ProjectLongTermPlansEntity
import io.cloudflight.jems.server.project.entity.description.ProjectLongTermPlansRow
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface ProjectLongTermPlansRepository : PagingAndSortingRepository<ProjectLongTermPlansEntity, Long> {

    fun findFirstByProjectId(projectId: Long): ProjectLongTermPlansEntity?

    @Query(
        """
            SELECT
             entity.project_id AS projectId,
             translation.language AS language,
             translation.project_ownership AS projectOwnership,
             translation.project_durability AS projectDurability,
             translation.project_transferability AS projectTransferability
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation ON entity.project_id = translation.project_id
             WHERE entity.project_id = :projectId
             ORDER BY entity.project_id
             """,
        nativeQuery = true
    )
    fun findByProjectIdAsOfTimestamp(projectId: Long, timestamp: Timestamp): List<ProjectLongTermPlansRow>

}
