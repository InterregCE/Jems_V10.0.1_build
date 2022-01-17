package io.cloudflight.jems.server.project.repository.partner.associated_organization

import io.cloudflight.jems.server.project.entity.associatedorganization.AssociatedOrganizationAddressRow
import io.cloudflight.jems.server.project.entity.associatedorganization.AssociatedOrganizationContactRow
import io.cloudflight.jems.server.project.entity.associatedorganization.AssociatedOrganizationDetailRow
import io.cloudflight.jems.server.project.entity.associatedorganization.AssociatedOrganizationSimpleRow
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganization
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganizationRow
import java.sql.Timestamp
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import org.springframework.data.jpa.repository.Query

interface ProjectAssociatedOrganizationRepository : JpaRepository<ProjectAssociatedOrganization, Long> {

    override fun findById(id: Long): Optional<ProjectAssociatedOrganization> {
        throw UnsupportedOperationException("use findFirstByProjectIdAndId")
    }

    override fun deleteById(id: Long) {
        throw UnsupportedOperationException("use delete with findFirstByProjectIdAndId")
    }

    fun findFirstByProjectIdAndId(projectId: Long, id: Long): Optional<ProjectAssociatedOrganization>

    @Query(
        """
            SELECT
             entity.partner_id AS partnerId
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             WHERE entity.id = :id AND entity.project_id = :projectId
             """,
        nativeQuery = true)
    fun getPartnerIdByProjectIdAndIdAsOfTimestamp(
        projectId: Long, id: Long, timestamp: Timestamp
    ): Long?

    fun findAllByProjectId(projectId: Long, pageable: Pageable): Page<ProjectAssociatedOrganization>

    fun findAllByProjectId(projectId: Long, sort: Sort): Iterable<ProjectAssociatedOrganization>

    fun findAllByProjectId(projectId: Long): Iterable<ProjectAssociatedOrganization>

    @Query(
        """
            SELECT
             entity.id as id,
             addresses.*,
             addresses.nuts_region2 AS nutsRegion2,
             addresses.nuts_region3 AS nutsRegion3,
             addresses.house_number AS houseNumber,
             addresses.postal_code AS postalCode
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_address FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS addresses ON entity.id = addresses.organization_id
             WHERE entity.id = :id
             ORDER BY entity.id
             """,
        nativeQuery = true
    )
    fun findAssociatedOrganizationAddressesByIdAsOfTimestamp(
        id: Long, timestamp: Timestamp
    ): List<AssociatedOrganizationAddressRow>

    @Query(
        """
            SELECT
             entity.*,
             entity.sort_number AS sortNumber,
             entity.name_in_original_language AS nameInOriginalLanguage,
             entity.name_in_english AS nameInEnglish,
             translation.*,
             translation.role_description AS roleDescription
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation ON entity.id = translation.organization_id
             WHERE entity.id = :id AND entity.project_id = :projectId
             ORDER BY entity.id
             """,
        nativeQuery = true
    )
    fun findFirstByProjectIdAndId(projectId: Long, id: Long, timestamp: Timestamp): List<ProjectAssociatedOrganizationRow>

    @Query(
        value = """
             SELECT
             entity.*,
             entity.sort_number AS sortNumber,
             entity.name_in_original_language AS nameInOriginalLanguage,
             entity.name_in_english AS nameInEnglish,
             (SELECT
                addresses.country
                from project_associated_organization_address
                FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS addresses
                WHERE entity.id = addresses.organization_id) as country,
            (SELECT
                partner.abbreviation
                from project_partner
                FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS partner
                WHERE entity.partner_id = partner.id) as partnerAbbreviation
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             WHERE entity.project_id = :projectId
             ORDER BY entity.id
             """,
        countQuery = """
             SELECT
             count(*)
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             WHERE entity.project_id = :projectId
             ORDER BY entity.id
             """,
        nativeQuery = true
    )
    fun findAllByProjectId(projectId: Long, pageable: Pageable, timestamp: Timestamp): Page<AssociatedOrganizationSimpleRow>

    @Query(
        """
            SELECT
             contacts.*,
             contacts.first_name AS firstName,
             contacts.last_name AS lastName
             FROM #{#entityName}_contact FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS contacts
             WHERE contacts.organization_id = :organizationId
             """,
        nativeQuery = true
    )
    fun findAssociatedOrganizationContactsByIdAsOfTimestamp(
        organizationId: Long, timestamp: Timestamp
    ): List<AssociatedOrganizationContactRow>


    @Query(
        """
            SELECT
             entity.id,
             entity.active,
             entity.name_in_original_language as nameInOriginalLanguage,
             entity.name_in_english as nameInEnglish,
             entity.sort_number as sortNumber,
             entityTranslation.language,
             entityTranslation.role_description as roleDescription,

             address.country,
             address.nuts_region2 as nutsRegion2,
             address.nuts_region3 as nutsRegion3,
             address.street,
             address.house_number as houseNumber,
             address.postal_code as postalCode,
             address.city,
             address.homepage,

             contact.type as contactType,
             contact.title,
             contact.first_name as firstName,
             contact.last_name as lastName,
             contact.email,
             contact.telephone,

             partner.id as partnerId,
             partner.active as partnerActive,
             partner.abbreviation,
             partner.role,
             partner.sort_number as partnerSortNumber,

             partnerAddress.country as partnerCountry,
             partnerAddress.nuts_region3 as partnerNutsRegion3

             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entityTranslation ON entity.id = entityTranslation.organization_id
             LEFT JOIN project_partner FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS partner ON entity.partner_id = partner.id
             LEFT JOIN project_partner_address FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS partnerAddress ON partner.id = partnerAddress.partner_id
             LEFT JOIN #{#entityName}_address FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS address ON entity.id = address.organization_id
             LEFT JOIN #{#entityName}_contact FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS contact ON entity.id = contact.organization_id
             WHERE entity.project_id = :projectId
             ORDER BY entity.id
             """,
        nativeQuery = true
    )
    fun findAllByProjectIdAsOfTimestamp(
        projectId: Long, timestamp: Timestamp
    ): List<AssociatedOrganizationDetailRow>
}
