package io.cloudflight.jems.server.project.repository.description

import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceBenefitRow
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceEntity
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceRow
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceSpfRecipientRow
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceStrategyRow
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceSynergyRow
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface ProjectRelevanceRepository : PagingAndSortingRepository<ProjectRelevanceEntity, Long> {

    fun findFirstByProjectId(projectId: Long): ProjectRelevanceEntity?

    @Query(
        """
            SELECT
             entity.project_id AS projectId,
             translation.language AS language,
             translation.territorial_challenge AS territorialChallenge,
             translation.common_challenge AS commonChallenge,
             translation.transnational_cooperation AS transnationalCooperation,
             translation.available_knowledge AS availableKnowledge
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation ON entity.project_id = translation.project_id
             WHERE entity.project_id = :projectId
             ORDER BY entity.project_id
             """,
        nativeQuery = true
    )
    fun findByProjectIdAsOfTimestamp(projectId: Long, timestamp: Timestamp): List<ProjectRelevanceRow>

    @Query(
        """
            SELECT
             entity.id AS id,
             entity.project_relevance_id AS projectId,
             entity.target_group AS targetGroup,
             translation.language AS language,
             translation.specification AS specification
             FROM #{#entityName}_benefit FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_benefit_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation ON entity.id = translation.source_entity_id
             WHERE entity.project_relevance_id = :projectId
             ORDER BY entity.sort_number
             """,
        nativeQuery = true
    )
    fun findBenefitsByProjectIdAsOfTimestamp(projectId: Long, timestamp: Timestamp): List<ProjectRelevanceBenefitRow>

    @Query(
        """
            SELECT
             entity.id AS id,
             entity.project_relevance_id AS projectId,
             entity.recipient_group AS recipientGroup,
             translation.language AS language,
             translation.specification AS specification
             FROM #{#entityName}_spf_recipient FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_spf_recipient_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation ON entity.id = translation.source_entity_id
             WHERE entity.project_relevance_id = :projectId
             ORDER BY entity.sort_number
        """,
        nativeQuery = true
    )
    fun findSpfRecipientsByProjectIdAsOfTimestamp(projectId: Long, timestamp: Timestamp): List<ProjectRelevanceSpfRecipientRow>

    @Query(
        """
            SELECT
             entity.id AS id,
             entity.project_relevance_id AS projectId,
             entity.strategy AS strategy,
             translation.language AS language,
             translation.specification AS specification
             FROM #{#entityName}_strategy FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_strategy_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation ON entity.id = translation.source_entity_id
             WHERE entity.project_relevance_id = :projectId
             ORDER BY entity.sort_number
             """,
        nativeQuery = true
    )
    fun findStrategiesByProjectIdAsOfTimestamp(projectId: Long, timestamp: Timestamp): List<ProjectRelevanceStrategyRow>

    @Query(
        """
            SELECT
             entity.id AS id,
             entity.project_relevance_id AS projectId,
             translation.language AS language,
             translation.synergy AS synergy,
             translation.specification AS specification
             FROM #{#entityName}_synergy FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_synergy_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation ON entity.id = translation.source_entity_id
             WHERE entity.project_relevance_id = :projectId
             ORDER BY entity.sort_number
             """,
        nativeQuery = true
    )
    fun findSynergiesByProjectIdAsOfTimestamp(projectId: Long, timestamp: Timestamp): List<ProjectRelevanceSynergyRow>

}
