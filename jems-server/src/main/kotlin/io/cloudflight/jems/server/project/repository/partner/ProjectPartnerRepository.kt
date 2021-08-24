package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.server.project.entity.partner.PartnerAddressRow
import io.cloudflight.jems.server.project.entity.partner.PartnerContactRow
import io.cloudflight.jems.server.project.entity.partner.PartnerIdentityRow
import io.cloudflight.jems.server.project.entity.partner.PartnerMotivationRow
import io.cloudflight.jems.server.project.entity.partner.PartnerSimpleRow
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.PartnerContributionRow
import io.cloudflight.jems.server.project.entity.partner.cofinancing.PartnerFinancingRow
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.util.Optional

@Repository
interface ProjectPartnerRepository : JpaRepository<ProjectPartnerEntity, Long> {

    fun existsByProjectIdAndId(projectId: Long, id: Long): Boolean

    fun findFirstByProjectIdAndId(projectId: Long, id: Long): Optional<ProjectPartnerEntity>

    fun findAllByProjectId(projectId: Long, pageable: Pageable): Page<ProjectPartnerEntity>

    fun findAllByProjectId(projectId: Long): Iterable<ProjectPartnerEntity>

    fun findTop30ByProjectId(projectId: Long, sort: Sort): Iterable<ProjectPartnerEntity>

    fun findFirstByProjectIdAndRole(projectId: Long, role: ProjectPartnerRole): Optional<ProjectPartnerEntity>

    fun existsByProjectIdAndAbbreviation(projectId: Long, name: String): Boolean


    fun countByProjectId(projectId: Long): Long

    @Query("SELECT e.project.id FROM project_partner e WHERE e.id = :partnerId")
    fun getProjectIdForPartner(partnerId: Long): Long?

    @Query(
        """
            SELECT
             entity.project_id AS projectId
             FROM #{#entityName} FOR SYSTEM_TIME ALL AS entity
             WHERE entity.id = :id
             ORDER BY entity.ROW_START DESC
             LIMIT 1
             """,
        nativeQuery = true)
    fun getProjectIdByPartnerIdInFullHistory(id: Long): Long?

    @Query(
        """
            SELECT
             financing.*,
             fund.id AS fundId,
             fund.type AS fundType,
             fund.selected AS selected,
             fundTransl.*
             FROM #{#entityName}_co_financing FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS financing
             LEFT JOIN programme_fund AS fund ON financing.programme_fund_id = fund.id
             LEFT JOIN programme_fund_transl AS fundTransl ON fund.id = fundTransl.source_entity_id
             WHERE financing.partner_id = :partnerId
             """,
        nativeQuery = true
    )
    fun findPartnerFinancingByIdAsOfTimestamp(
        partnerId: Long, timestamp: Timestamp
    ): List<PartnerFinancingRow>

    @Query(
        """
            SELECT
             contribution.*
             FROM #{#entityName}_contribution FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS contribution
             WHERE contribution.partner_id = :partnerId
             """,
        nativeQuery = true
    )
    fun findPartnerContributionByIdAsOfTimestamp(
        partnerId: Long, timestamp: Timestamp
    ): List<PartnerContributionRow>

    @Query(
        """
            SELECT
             entity.*,
             entity.project_id AS projectId,
             entity.legal_status_id AS legalStatusId,
             entity.sort_number AS sortNumber,
             entity.name_in_original_language AS nameInOriginalLanguage,
             entity.name_in_english AS nameInEnglish,
             entity.partner_type AS partnerType,
             entity.vat_recovery AS vatRecovery,
             translation.*
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation ON entity.id = translation.partner_id
             WHERE entity.id = :id
             ORDER BY entity.id
             """,
        nativeQuery = true
    )
    fun findPartnerIdentityByIdAsOfTimestamp(
        id: Long, timestamp: Timestamp
    ): List<PartnerIdentityRow>

    @Query(
        """
            SELECT
             addresses.*,
             addresses.nuts_region2 AS nutsRegion2,
             addresses.nuts_region3 AS nutsRegion3,
             addresses.house_number AS houseNumber,
             addresses.postal_code AS postalCode
             FROM #{#entityName}_address FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS addresses
             WHERE addresses.partner_id = :partnerId
             """,
        nativeQuery = true
    )
    fun findPartnerAddressesByIdAsOfTimestamp(
        partnerId: Long, timestamp: Timestamp
    ): List<PartnerAddressRow>

    @Query(
        """
            SELECT
             contacts.*,
             contacts.first_name AS firstName,
             contacts.last_name AS lastName
             FROM #{#entityName}_contact FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS contacts
             WHERE contacts.partner_id = :partnerId
             """,
        nativeQuery = true
    )
    fun findPartnerContactsByIdAsOfTimestamp(
        partnerId: Long, timestamp: Timestamp
    ): List<PartnerContactRow>

    @Query(
        """
            SELECT
             entity.id AS partnerId,
             motivationTransl.*,
             motivationTransl.organization_relevance AS organizationRelevance,
             motivationTransl.organization_role AS organizationRole,
             motivationTransl.organization_experience AS organizationExperience
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_motivation_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS motivationTransl ON entity.id = motivationTransl.partner_id
             WHERE entity.id = :partnerId
             ORDER BY entity.id
             """,
        nativeQuery = true
    )
    fun findPartnerMotivationByIdAsOfTimestamp(
        partnerId: Long, timestamp: Timestamp
    ): List<PartnerMotivationRow>

    @Query(
        value = """
             SELECT
             entity.id as id,
             entity.abbreviation as abbreviation,
             entity.role as role,
             entity.sort_number as sortNumber,
             addresses.country as country
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_address FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS addresses ON entity.id = addresses.partner_id
             WHERE entity.project_id = :projectId AND (addresses.type = 'Organization' || addresses.type IS NULL)
             """,
        countQuery = """
             SELECT
             count(*)
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             WHERE entity.project_id = :projectId
             """,
        nativeQuery = true
    )
    fun findAllByProjectIdAsOfTimestamp(
        projectId: Long,
        pageable: Pageable,
        timestamp: Timestamp
    ): Page<PartnerSimpleRow>

    @Query(
        """
             SELECT
             entity.*,
             entity.sort_number as sortNumber,
             addresses.*
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_address FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS addresses ON entity.id = addresses.partner_id
             WHERE entity.project_id = :projectId AND (addresses.type = 'Organization' || addresses.type IS NULL)
             ORDER BY entity.sort_number ASC
             LIMIT 30
             """,
        nativeQuery = true
    )
    fun findTop30ByProjectIdSortBySortNumberAsOfTimestamp(
        projectId: Long,
        timestamp: Timestamp
    ): List<PartnerSimpleRow>

    @Query(
        value = """
             SELECT
             entity.*,
             (SELECT
                addresses.country
                from project_partner_address
                FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS addresses
                WHERE entity.id = addresses.partner_id AND addresses.type = 'Organization') as country
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             WHERE entity.id = :partnerId
             ORDER BY entity.id
             """,
        nativeQuery = true
    )
    fun findOneByIdAsOfTimestamp(partnerId: Long, timestamp: Timestamp): PartnerSimpleRow
}
